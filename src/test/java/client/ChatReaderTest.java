package client;

import loggerService.Logger;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ChatReaderTest {

    static Scanner scanner;
    static ChatReader chatReader;
    static Logger logger;
    static Socket socket;
    static BufferedReader bufferedReader;

    @BeforeAll
    static void beforeAll() {
        logger = Mockito.mock(Logger.class);
        Mockito.doNothing().when(logger).log(Mockito.anyString());
        socket = Mockito.mock(Socket.class);
        scanner = new Scanner(System.in);
        chatReader = new ChatReader(null, "ChatReader", socket, "Mary", logger, scanner);
    }

    @BeforeEach
    void initTest() {
        bufferedReader = Mockito.mock(BufferedReader.class);
    }

    @Test
    void test_receiveMessage_success() throws IOException {
        Mockito.when(bufferedReader.readLine()).thenReturn("{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}");

        String result = chatReader.receiveMessage(bufferedReader);
        String expected = "{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void test_receiveMessage_exception() throws IOException {
        Mockito.when(bufferedReader.readLine()).thenThrow(IOException.class);

        assertDoesNotThrow(() -> {
            chatReader.receiveMessage(bufferedReader);
        });
    }

    @AfterAll
    static void afterAll() {
        scanner.close();
    }
}
