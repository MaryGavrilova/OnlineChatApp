package server;

import loggerService.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MonoThreadClientMessageHandlerTest {

    static Logger logger;
    static ChatParticipant chatParticipant;
    static List<ChatParticipant> chatParticipantsList;
    static BufferedReader bufferedReader;
    static BufferedWriter bufferedWriter;
    static MonoThreadClientMessageHandler monoThreadClientMessageHandler;
    static String jsonText;

    @BeforeAll
    static void beforeAll() {
        logger = Mockito.mock(Logger.class);
        Mockito.doNothing().when(logger).log(Mockito.anyString());
        jsonText = "{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}";
    }

    @BeforeEach
    void initTest() {
        bufferedReader = Mockito.mock(BufferedReader.class);
        bufferedWriter = Mockito.mock(BufferedWriter.class);
        chatParticipant = Mockito.mock(ChatParticipant.class);
        Mockito.when(chatParticipant.getBufferedReader()).thenReturn(bufferedReader);
        Mockito.when(chatParticipant.getBufferedWriter()).thenReturn(bufferedWriter);
        chatParticipantsList = List.of(chatParticipant, chatParticipant, chatParticipant);
        monoThreadClientMessageHandler =
                new MonoThreadClientMessageHandler(chatParticipant, logger, chatParticipantsList);
    }

    @Test
    void test_receiveMessage_success() throws IOException {
        Mockito.when(chatParticipant.getBufferedReader().readLine()).thenReturn("{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}");

        String result = monoThreadClientMessageHandler.receiveMessage();
        String expected = "{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void test_receiveMessage_exception() throws IOException {
        Mockito.when(chatParticipant.getBufferedReader().readLine()).thenThrow(IOException.class);

        assertDoesNotThrow((Executable) monoThreadClientMessageHandler::receiveMessage);
    }

    @Test
    void test_sendMessage_exceptionInWriteMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(bufferedWriter).write(Mockito.anyString());

        assertDoesNotThrow(() -> {
            monoThreadClientMessageHandler.sendMessage(jsonText);
        });
    }

    @Test
    void test_sendMessage_exceptionInNewLineMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(bufferedWriter).newLine();

        assertDoesNotThrow(() -> {
            monoThreadClientMessageHandler.sendMessage(jsonText);
        });
    }

    @Test
    void test_sendMessage_exceptionInFlushMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(bufferedWriter).flush();

        assertDoesNotThrow(() -> {
            monoThreadClientMessageHandler.sendMessage(jsonText);
        });
    }
}
