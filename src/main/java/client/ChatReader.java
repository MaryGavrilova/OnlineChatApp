package client;

import loggerService.Logger;
import messageService.Message;
import messageService.MessageJsonFileConverter;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import static client.Client.COMMAND_TO_EXIT;
import static client.Client.NAME_OF_LOG_FILE;

public class ChatReader extends Thread {
    protected Socket socket;
    protected String chatParticipantName;

    public ChatReader(ThreadGroup group, String name, Socket socket, String chatParticipantName) {
        super(group, name);
        this.socket = socket;
        this.chatParticipantName = chatParticipantName;
    }

    @Override
    public void run() {
        Logger logger = new Logger(NAME_OF_LOG_FILE);
        while (!socket.isClosed()) {

            // создаем временный файл для записи сообщения, поступившего в чат от сервера
            String fileName = "messageFromChat" + Thread.currentThread().getName() + ".json";
            createMessageFile(fileName);

            // получаем сообщение от сервера
            receiveMessage(fileName, socket);

            // парсим json файл с сообщением в объект класса Message
            MessageJsonFileConverter messageJsonFileConverter = new MessageJsonFileConverter();
            Message message = messageJsonFileConverter.parseJsonFileToMessage(fileName);

            // печатаем сообщение в консоль
            System.out.println(message);

            // записываем полученное из чата сообщение с указанием имени пользователя и времени отправки в файл file.log
            logger.log(message.toString());

            // удаляем временный файл с сообщением
            deleteMessageFile(fileName);

            // проверяем условия продолжения работы потока
            if (message.getSenderName().equals(chatParticipantName) & message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                System.out.println("Сlient initiated connection closure");
                break;
            }
            // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
        }
        // если условие выхода - верно, выходим из цикла и завершаем работу потока
    }

    public static void createMessageFile(String fileName) {
        File messageFile = new File(fileName);
        try {
            if (messageFile.createNewFile()) {
                System.out.println("File " + fileName + " created");
            } else {
                System.out.println("File " + fileName + " already exists");
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static void receiveMessage(String fileName, Socket client) {
        // открываем канал чтения из сокета
        try (BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            System.out.println("BufferedInputStream created");

            byte[] bytes = new byte[1024];
            int data;
            while ((data = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, data);
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static void deleteMessageFile(String fileName) {
        try {
            if (Files.deleteIfExists(Paths.get(fileName))) {
                System.out.println("File " + fileName + " deleted");
            } else {
                System.out.println("File " + fileName + " is not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

