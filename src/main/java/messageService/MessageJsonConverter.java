package messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MessageJsonConverter {

    public Message parseJsonToMessage(String json) throws NullPointerException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Message message = gson.fromJson(json, Message.class);
        if (message == null) {
            throw new NullPointerException("Message can not be null");
        } else {
            return message;
        }
    }

    public String convertMessageToJson(Message message) {
        if (message == null) {
            throw new NullPointerException("Message can not be null");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.toJson(message);
        }
    }
}

