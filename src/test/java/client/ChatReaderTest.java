package client;

import loggerService.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ChatReaderTest {
    @Mock
    static Logger logger = Mockito.mock(Logger.class);
    static Socket socket = Mockito.mock(Socket.class);

    @BeforeAll
    static void beforeAll() {
        Mockito.doNothing().when(logger).log(Mockito.anyString());
    }

    @Test
    void test_receiveMessage_success() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String chatParticipantName = "Mary";

        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(bufferedReader.readLine()).thenReturn("{\"senderName\":\"Mary\",\"message\":\"Hello!\"}");

        ChatReader chatReader = new ChatReader(null, "ChatReader", socket, chatParticipantName, logger, scanner);
        String result = chatReader.receiveMessage(bufferedReader);

        String expected = "{\"senderName\":\"Mary\",\"message\":\"Hello!\"}";

        Assertions.assertEquals(expected, result);

    }

    @Test
    void test_receiveMessage_exception() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String chatParticipantName = "Mary";

        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(bufferedReader.readLine()).thenThrow(IOException.class);

        ChatReader chatReader = new ChatReader(null, "ChatReader", socket, chatParticipantName, logger, scanner);

        assertDoesNotThrow(() -> {
            chatReader.receiveMessage(bufferedReader);
        });
    }
}
