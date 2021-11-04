package client;

import loggerService.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ChatWriterTest {

    static Scanner scanner;

    @Mock
    static Logger logger = Mockito.mock(Logger.class);
    static Socket socket = Mockito.mock(Socket.class);

    @BeforeAll
    static void beforeAll() {
        Mockito.doNothing().when(logger).log(Mockito.anyString());
        scanner = new Scanner(System.in);
    }

    @Test
    void test_sendMessage_exceptionInWriteMethod() throws IOException {
        String chatParticipantName = "Mary";

        BufferedWriter bufferedWriter = Mockito.mock(BufferedWriter.class);
        Mockito.doThrow(IOException.class).when(bufferedWriter).write(Mockito.anyString());

        ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, chatParticipantName, logger, scanner);

        assertDoesNotThrow(() -> {
            chatWriter.sendMessage(bufferedWriter, Mockito.anyString());
        });
    }

    @Test
    void test_sendMessage_exceptionInNewLineMethod() throws IOException {
        String chatParticipantName = "Mary";

        BufferedWriter bufferedWriter = Mockito.mock(BufferedWriter.class);
        Mockito.doThrow(IOException.class).when(bufferedWriter).newLine();

        ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, chatParticipantName, logger, scanner);

        assertDoesNotThrow(() -> {
            chatWriter.sendMessage(bufferedWriter, Mockito.anyString());
        });
    }

    @Test
    void test_sendMessage_exceptionInFlushMethod() throws IOException {
        String chatParticipantName = "Mary";

        BufferedWriter bufferedWriter = Mockito.mock(BufferedWriter.class);
        Mockito.doThrow(IOException.class).when(bufferedWriter).flush();

        ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, chatParticipantName, logger, scanner);

        assertDoesNotThrow(() -> {
            chatWriter.sendMessage(bufferedWriter, Mockito.anyString());
        });
    }

    @AfterAll
    static void afterAll() {
        scanner.close();
    }
}
