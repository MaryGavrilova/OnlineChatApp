package messageService;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class MessageBuilder {
    protected String senderName;
    protected String messageText;

    public MessageBuilder() {
    }

    public MessageBuilder setSenderName(String senderName) throws IllegalArgumentException {
        if (StringUtils.isBlank(senderName)) {
            throw new IllegalArgumentException("Sender name is null/empty/blank");
        } else {
            this.senderName = senderName;
        }
        return this;
    }

    public MessageBuilder setMessageText(String messageText) throws IllegalArgumentException {
        if (StringUtils.isBlank(messageText)) {
            throw new IllegalArgumentException("Message is null/empty/blank");
        } else {
            this.messageText = messageText;
        }
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public Message build() throws IllegalStateException {
        if (senderName == null || messageText == null) {
            throw new IllegalStateException("Not all message parameters are set");
        }
        return new Message(senderName, messageText);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageBuilder that = (MessageBuilder) o;
        return Objects.equals(senderName, that.senderName) && Objects.equals(messageText, that.messageText);
    }
}
