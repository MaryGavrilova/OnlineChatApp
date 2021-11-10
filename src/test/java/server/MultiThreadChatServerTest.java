package server;

import loggerService.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static server.MultiThreadChatServer.readServerSettingsFile;

public class MultiThreadChatServerTest {

    static Logger logger;
    static Path path;

    @BeforeAll
    static void beforeAll() {
        logger = Mockito.mock(Logger.class);
        Mockito.doNothing().when(logger).log(Mockito.anyString());
        path = Paths.get("setting.txt");
    }

    @Test
    void test_readServerSettingsFile_success() throws IOException {
        Files.createFile(path);
        Files.writeString(path, "8888", StandardCharsets.UTF_8);
        int expected = 8888;
        int result = readServerSettingsFile(logger, path);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void test_readServerSettingsFile_exception_notExistFile() {
        assertDoesNotThrow(() -> {
            readServerSettingsFile(logger, path);
        });
    }

    @Test
    void test_readServerSettingsFile_exception_fileWithNullContent() throws IOException {
        Files.createFile(path);
        assertDoesNotThrow(() -> {
            readServerSettingsFile(logger, path);
        });
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("setting.txt"));
    }
}
