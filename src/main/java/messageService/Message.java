package messageService;

import java.util.Objects;

public class Message {

    protected final String senderName;
    protected final String messageText;

    public Message(String senderName, String messageText) {
        this.senderName = senderName;
        this.messageText = messageText;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    @Override
    public String toString() {
        return "From: " + senderName +
                " Message: " + messageText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(senderName, message1.senderName) && Objects.equals(messageText, message1.messageText);
    }
}
