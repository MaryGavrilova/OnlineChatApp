package server;

import loggerService.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MonoThreadClientMessageHandlerTest {
    @Mock
    static Logger logger = Mockito.mock(Logger.class);

    @BeforeAll
    static void beforeAll() {
        Mockito.doNothing().when(logger).log(Mockito.anyString());
    }

    @Test
    void test_receiveMessage_success() throws IOException {
        List<ChatParticipant> chatParticipantsList = new CopyOnWriteArrayList<>();
        ChatParticipant chatParticipant = Mockito.mock(ChatParticipant.class);
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(chatParticipant.getBufferedReader()).thenReturn(bufferedReader);
        Mockito.when(chatParticipant.getBufferedReader().readLine()).thenReturn("{\"senderName\":\"Mary\",\"message\":\"Hello!\"}");

        MonoThreadClientMessageHandler monoThreadClientMessageHandler =
                new MonoThreadClientMessageHandler(chatParticipant, logger, chatParticipantsList);
        String result = monoThreadClientMessageHandler.receiveMessage();

        String expected = "{\"senderName\":\"Mary\",\"message\":\"Hello!\"}";

        Assertions.assertEquals(expected, result);

    }

    @Test
    void test_receiveMessage_exception() throws IOException {
        List<ChatParticipant> chatParticipantsList = new CopyOnWriteArrayList<>();
        ChatParticipant chatParticipant = Mockito.mock(ChatParticipant.class);
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(chatParticipant.getBufferedReader()).thenReturn(bufferedReader);
        Mockito.when(chatParticipant.getBufferedReader().readLine()).thenThrow(IOException.class);

        MonoThreadClientMessageHandler monoThreadClientMessageHandler =
                new MonoThreadClientMessageHandler(chatParticipant, logger, chatParticipantsList);

        assertDoesNotThrow((Executable) monoThreadClientMessageHandler::receiveMessage);
    }

    @Test
    void test_sendMessage_exceptionInWriteMethod() throws IOException {
        String jsonText = "{\"senderName\":\"Mary\",\"message\":\"Hello!\"}";
        ChatParticipant chatParticipant = Mockito.mock(ChatParticipant.class);
        ChatParticipant currentChatParticipant = Mockito.mock(ChatParticipant.class);
        List<ChatParticipant> chatParticipantsList = List.of(currentChatParticipant, currentChatParticipant, currentChatParticipant);

        BufferedWriter bufferedWriter = Mockito.mock(BufferedWriter.class);
        Mockito.when(currentChatParticipant.getBufferedWriter()).thenReturn(bufferedWriter);
        Mockito.doThrow(IOException.class).when(bufferedWriter).write(Mockito.anyString());

        MonoThreadClientMessageHandler monoThreadClientMessageHandler =
                new MonoThreadClientMessageHandler(chatParticipant, logger, chatParticipantsList);

        assertDoesNotThrow(() -> {
            monoThreadClientMessageHandler.sendMessage(jsonText);
        });
    }
}
