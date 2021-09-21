package server;

import loggerService.Logger;
import messageService.Message;
import messageService.MessageJsonFileConverter;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static server.MultiThreadChatServer.*;

public class MonoThreadClientMessageHandler implements Runnable {

    protected Socket client;

    public MonoThreadClientMessageHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        Logger logger = new Logger(NAME_OF_LOG_FILE);
        while (!client.isClosed()) {
            System.out.println("Server is processing message from client " + Thread.currentThread().getName());

            // создаем временный файл для записи сообщения от клиента
            String fileName = "messageFrom" + Thread.currentThread().getName() + ".json";
            createMessageFile(fileName);

            //получаем сообщение и записываем во временный файл
            receiveMessage(fileName, client);

            // парсим json файл с сообщением в объект класса Message
            MessageJsonFileConverter messageJsonFileConverter = new MessageJsonFileConverter();
            Message message = messageJsonFileConverter.parseJsonFileToMessage(fileName);

            // отправляем сообщение в общий чат путем рассылки каждому участнику
            sendMessageToChat(chatParticipantsList, fileName);

            // записываем отправленное в чат сообщение с указанием имени пользователя и времени отправки в файл логирования
            logger.log(message.toString());

            // удаляем временный файл с сообщением
            deleteMessageFile(fileName);

            // проверяем условия продолжения работы с клиентом
            if (message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                System.out.println("Сlient " + Thread.currentThread().getName() + " initiated connection closure");
                break;
            }
            // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
        }

        // если условие выхода - верно, закрываем сокет общения с клиентом и удаляем клиента из списка участников чата
        try {
            client.close();
            System.out.println("Client disconnected");
            if (chatParticipantsList.remove(client)) {
                System.out.println("Client is deleted from chat participants' list");
            } else {
                System.out.println("Client is not found in chat participants' list");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void sendMessageToChat(List<Socket> chatParticipantsList, String filename) {
        for (int i = 0; i < chatParticipantsList.size(); i++) {
            Socket chatParticipant = chatParticipantsList.get(i);
            // открываем канал записи в сокет
            try (BufferedOutputStream bos = new BufferedOutputStream(chatParticipant.getOutputStream());
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

