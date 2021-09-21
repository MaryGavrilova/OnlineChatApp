package messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class MessageJsonFileConverter {

    public Message parseJsonFileToMessage(String fileName) {
        String jsonString = null;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            jsonString = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonString, Message.class);
    }

    public void writeMessageToJsonFile(Message message, String fileName) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String jsonString = gson.toJson(message);
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
