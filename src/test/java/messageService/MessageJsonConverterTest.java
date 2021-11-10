package messageService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageJsonConverterTest {

    static MessageJsonConverter messageJsonConverter;

    @BeforeAll
    static void beforeAll() {
        messageJsonConverter = new MessageJsonConverter();
    }

    @Test
    void test_parseJsonToMessage_success(){
        Message expected = new Message("Mary", "Hello!");
        Message result = messageJsonConverter.parseJsonToMessage("{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}");

        Assertions.assertEquals(expected, result);
    }

    @Test
    void test_convertMessageToJson_success(){
        String expected = "{\"senderName\":\"Mary\",\"messageText\":\"Hello!\"}";
        String result = messageJsonConverter.convertMessageToJson(new Message("Mary", "Hello!"));

        Assertions.assertEquals(expected, result);
    }

    @Test
    void test_parseJsonToMessage_nullPointerException(){
        assertThrows(NullPointerException.class,
                () -> {
                    messageJsonConverter.parseJsonToMessage(null);
                });
    }

    @Test
    void test_parseJsonToMessage_illegalArgumentException_nullCase(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageJsonConverter.parseJsonToMessage("{\"senderName\":\"Mary\"}");
                });
    }

    @Test
    void test_parseJsonToMessage_illegalArgumentException_emptyCase(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageJsonConverter.parseJsonToMessage("{\"senderName\":\"Mary\",\"messageText\":\"\"}");
                });
    }

    @Test
    void test_parseJsonToMessage_illegalArgumentException_blankCase(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageJsonConverter.parseJsonToMessage("{\"senderName\":\"Mary\",\"messageText\":\"  \"}");
                });
    }

    @Test
    void test_convertMessageToJson_nullPointerException(){
        assertThrows(NullPointerException.class,
                () -> {
                    messageJsonConverter.convertMessageToJson(null);
                });
    }

    @Test
    void test_convertMessageToJson_illegalArgumentException_nullCase(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageJsonConverter.convertMessageToJson(new Message("Mary", null));
                });
    }

    @Test
    void test_convertMessageToJson_illegalArgumentException_emptyCase(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageJsonConverter.convertMessageToJson(new Message("Mary", ""));
                });
    }

    @Test
    void test_convertMessageToJson_illegalArgumentException_blankCase(){
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageJsonConverter.convertMessageToJson(new Message("Mary", " "));
                });
    }
}



