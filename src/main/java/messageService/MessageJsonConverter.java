package messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageJsonConverter {

    public Message parseJsonToMessage(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, Message.class);
    }

    public String convertMessageToJson(Message message) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(message);
    }
}
