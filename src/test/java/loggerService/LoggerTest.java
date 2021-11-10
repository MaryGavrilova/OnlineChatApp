package loggerService;

import org.junit.jupiter.api.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;

public class LoggerTest {
    static SimpleDateFormat simpleDateFormat;
    static String message;
    static FileWriter fileWriter;
    static Logger logger;

    @BeforeAll
    static void beforeAll() {
        simpleDateFormat = new SimpleDateFormat("2021:10:31  17:51:32");
        message = "ChatWriter: message is sent to chat";
    }

    @BeforeEach
    void initTest() throws IOException {
        fileWriter = Mockito.mock(FileWriter.class);
        logger = new Logger(fileWriter, simpleDateFormat);
    }

    @Test
    void log_success_notBlankMessage() throws IOException {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        logger.log(message);

        Mockito.verify(fileWriter, times(1)).write(argumentCaptor.capture());
        Assertions.assertEquals("[2021:10:31  17:51:32] ChatWriter: message is sent to chat\n", argumentCaptor.getValue());
    }

    @Test
    void log_success_blankMessage() throws IOException {
        String blankMessage = "";

        logger.log(blankMessage);

        Mockito.verify(fileWriter, Mockito.never()).write(Mockito.anyString());
    }

    @Test
    void log_exceptionInWriteMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(fileWriter).write(Mockito.anyString());

        assertDoesNotThrow(() -> {
            logger.log(message);
        });
    }

    @Test
    void log_exceptionInFlushMethod() throws IOException {
        Mockito.doThrow(IOException.class).when(fileWriter).flush();

        assertDoesNotThrow(() -> {
            logger.log(message);
        });
    }
}
