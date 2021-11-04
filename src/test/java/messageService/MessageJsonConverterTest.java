package messageService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageJsonConverterTest {
    @Test
    void test_parseJsonToMessage_validArguments_success() {
        String json = "{\"senderName\":\"Mary\",\"message\":\"Hello!\"}";
        Message exceptedMessage = new Message("Mary", "Hello!");

        MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
        Message resultMessage = messageJsonConverter.parseJsonToMessage(json);

        Assertions.assertEquals(exceptedMessage, resultMessage);
    }

    @Test
    void test_parseJsonToMessage_nullJson_exception() {
        String json = null;

        MessageJsonConverter messageJsonConverter = new MessageJsonConverter();

        assertThrows(NullPointerException.class, () -> {
            messageJsonConverter.parseJsonToMessage(json);
        });
    }

    @Test
    void test_parseJsonToMessage_emptyJson_exception() {
        String json = "";

        MessageJsonConverter messageJsonConverter = new MessageJsonConverter();

        assertThrows(NullPointerException.class, () -> {
            messageJsonConverter.parseJsonToMessage(json);
        });
    }
}



