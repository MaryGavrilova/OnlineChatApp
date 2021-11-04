package server;

import loggerService.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static client.Client.readClientSettingsFile;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MultiThreadChatServerTest {
    @Mock
    static Logger logger = Mockito.mock(Logger.class);

    @BeforeAll
    static void beforeAll(){
        Mockito.doNothing().when(logger).log(Mockito.anyString());
    }

    @Test
    void test_readServerSettingsFile_success() throws IOException {
       Path path = Paths.get("setting.txt");

        Files.createFile(path);
        File file = new File("setting.txt");
        file.createNewFile();
        Files.writeString(Paths.get("setting.txt"), "8888", StandardCharsets.UTF_8);
        int expected = 8888;
        int result = readClientSettingsFile(logger, path.toFile());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void test_readServerSettingsFile_exception_notExistFile() {
        File notExistFile = new File("setting.txt");
        assertDoesNotThrow(() -> {
            readClientSettingsFile(logger, notExistFile);
        });
    }


    @Test
    void test_readServerSettingsFile_exception_fileWithNullContent() throws IOException {
        File file = new File("setting.txt");
        file.createNewFile();
        assertDoesNotThrow(() -> {
            readClientSettingsFile(logger, file);
        });
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("setting.txt"));
    }
}
