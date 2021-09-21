package client;

import messageService.Message;
import messageService.MessageJsonFileConverter;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static client.Client.COMMAND_TO_EXIT;

public class ChatWriter extends Thread {
    protected Socket socket;
    protected String chatParticipantName;

    public ChatWriter(ThreadGroup group, String name, Socket socket, String chatParticipantName) {
        super(group, name);
        this.socket = socket;
        this.chatParticipantName = chatParticipantName;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            // пользователь пишет сообщение
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите текст сообщения:");
            String inputMessageText = scanner.nextLine();

            // создаем объект класса Message
            Message message = new Message(chatParticipantName, inputMessageText);

            // создаем временный файл для записи сообщения
            String fileName = "messageFrom" + chatParticipantName + Thread.currentThread().getName() + ".json";
            createMessageFile(fileName);

            // конвертируем объект класса Message в JSON file
            MessageJsonFileConverter messageJsonFileConverter = new MessageJsonFileConverter();
            messageJsonFileConverter.writeMessageToJsonFile(message, fileName);

            // отправляем сообщение серверу
            sendMessageToChat(socket, fileName);

            // удаляем файл с сообщением
            deleteMessageFile(fileName);

            // проверяем условия продолжения работы потока
            if (message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                System.out.println("Сlient initiated connection closure");
                break;
            }
            // если условие выхода - неверно, возвращаемся в начало для написания нового сообщения
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

    public static void sendMessageToChat(Socket socket, String filename) {
        // открываем канал записи в сокет
        try (BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename))) {
            System.out.println("BufferedOutputStream created");

            byte[] bytes = new byte[1024];
            int data;
            while ((data = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, data);
            }
            bos.flush();

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





