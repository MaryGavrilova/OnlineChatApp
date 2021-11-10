package messageService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageBuilderTest {

    static MessageBuilder messageBuilder;

    @BeforeEach
    void initTest() {
        messageBuilder = new MessageBuilder();
    }

    @Test
    void setSenderName_exception_emptySenderName() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageBuilder.setSenderName("");
                });
    }

    @Test
    void setSenderName_exception_nullSenderName() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageBuilder.setSenderName(null);
                });
    }

    @Test
    void setSenderName_exception_blankSenderName() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageBuilder.setSenderName(" ");
                });
    }

    @Test
    void setMessageText_exception_emptyMessageText() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageBuilder.setMessageText("");
                });
    }

    @Test
    void setMessageText_exception_nullMessageText() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageBuilder.setMessageText(null);
                });
    }

    @Test
    void setMessageText_exception_blankMessageText() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    messageBuilder.setMessageText(" ");
                });
    }

    @Test
    void build_exception() {
        messageBuilder.setMessageText("message");
        assertThrows(IllegalStateException.class,
                () -> {
                    messageBuilder.build();
                });
    }

    @Test
    void build_success() {
        Message expected = new Message("name", "message");

        messageBuilder.setSenderName("name");
        messageBuilder.setMessageText("message");
        Message result = messageBuilder.build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void setSenderName_success() {
        MessageBuilder expected = new MessageBuilder();
        expected.senderName = "name";

        MessageBuilder result = messageBuilder.setSenderName("name");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void setMessageText_success() {
        MessageBuilder expected = new MessageBuilder();
        expected.messageText = "message";

        MessageBuilder result = messageBuilder.setMessageText("message");
        Assertions.assertEquals(expected, result);
    }
}
