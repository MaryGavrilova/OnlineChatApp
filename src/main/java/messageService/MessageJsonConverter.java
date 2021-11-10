package messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;


public class MessageJsonConverter {

    public Message parseJsonToMessage(String json) throws NullPointerException, IllegalArgumentException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Message message = gson.fromJson(json, Message.class);
        if (message == null) {
            throw new NullPointerException("Message is null");
        }
        if (StringUtils.isBlank(message.getMessageText()) || StringUtils.isBlank(message.getSenderName())) {
            throw new IllegalArgumentException("Sender name or message text is null/empty/blank");
        } else {
            return message;
        }
    }

    public String convertMessageToJson(Message message) throws NullPointerException, IllegalArgumentException {
        if (message == null) {
            throw new NullPointerException("Message is null");
        }
        if (StringUtils.isBlank(message.getMessageText())|| StringUtils.isBlank(message.getSenderName())) {
            throw new IllegalArgumentException("Sender name or message text is null/empty/blank");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.toJson(message);
        }
    }
}

