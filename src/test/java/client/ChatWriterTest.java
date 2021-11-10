package client;

import loggerService.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ChatWriterTest {

    static Scanner scanner;
    static ChatWriter chatWriter;
    static Logger logger;
    static Socket socket;
    static BufferedWriter bufferedWriter;

    @BeforeAll
    static void beforeAll() {
        logger = Mockito.mock(Logger.class);
        Mockito.doNothing().when(logger).log(Mockito.anyString());
        socket = Mockito.mock(Socket.class);
        scanner = new Scanner(System.in);
        chatWriter = new ChatWriter(null, "ChatWriter", socket, "Mary", logger, scanner);
    }

    @BeforeEach
    void initTest() {
        bufferedWriter = Mockito.mock(BufferedWriter.class);
    }

    @Test
    void test_sendMessage_exceptionInWriteMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(bufferedWriter).write(Mockito.anyString());

        assertDoesNotThrow(() -> {
            chatWriter.sendMessage(bufferedWriter, Mockito.anyString());
        });
    }

    @Test
    void test_sendMessage_exceptionInNewLineMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(bufferedWriter).newLine();

        assertDoesNotThrow(() -> {
            chatWriter.sendMessage(bufferedWriter, Mockito.anyString());
        });
    }

    @Test
    void test_sendMessage_exceptionInFlushMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(bufferedWriter).flush();

        assertDoesNotThrow(() -> {
            chatWriter.sendMessage(bufferedWriter, Mockito.anyString());
        });
    }

    @AfterAll
    static void afterAll() {
        scanner.close();
    }
}
