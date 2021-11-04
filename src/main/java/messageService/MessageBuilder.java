package messageService;

import org.apache.commons.lang3.StringUtils;
// как сделать чтобы он обязывал обрабатывать экспешн
public class MessageBuilder {
    protected String senderName;
    protected String messageText;

    public MessageBuilder() {
    }

    public MessageBuilder setSenderName(String senderName) {
        if (StringUtils.isBlank(senderName)) {
            throw new IllegalArgumentException("Sender name is empty");
        } else {
            this.senderName = senderName;
        }
        return this;
    }

    public MessageBuilder setMessageText(String messageText) {
        if (StringUtils.isBlank(messageText)) {
            throw new IllegalArgumentException("Message is empty");
        } else {
            this.messageText = messageText;
        }
        return this;
    }

    public Message build() {
        if (senderName == null || messageText == null) {
            throw new IllegalStateException("Not all message parameters are set");
        }
        return new Message(senderName, messageText);
    }
}
