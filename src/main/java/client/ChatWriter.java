package client;

import loggerService.Logger;
import messageService.Message;
import messageService.MessageJsonConverter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static client.Client.*;

public class ChatWriter extends Thread {
    protected Socket socket;
    protected String chatParticipantName;
    protected Logger logger;
    protected Scanner scanner;

    public ChatWriter(ThreadGroup group, String name, Socket socket, String chatParticipantName, Logger logger, Scanner scanner) {
        super(group, name);
        this.socket = socket;
        this.chatParticipantName = chatParticipantName;
        this.logger = logger;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        try {
            // открываем поток записи
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (!socket.isClosed()) {
                // пользователь пишет сообщение
                System.out.println("Введите текст сообщения:");
                String messageText = scanner.nextLine();

                // создаем объект класса Message
                Message message = new Message(chatParticipantName, messageText);
                System.out.println(Thread.currentThread().getName() + ": message is prepared");
                logger.log(Thread.currentThread().getName() + ": message is prepared");

                // конвертируем объект класса Message в JSON file
                MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                String jsonText = messageJsonConverter.convertMessageToJson(message);

                // отправляем сообщение серверу
                sendMessageToChat(bufferedWriter, jsonText);
                System.out.println(Thread.currentThread().getName() + ": message is sent to chat");
                logger.log(Thread.currentThread().getName() + ": message is sent to chat");
                logger.log(message.toString());

                // проверяем условия продолжения работы потока
                if (message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                    System.out.println(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    logger.log(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    break;
                }
                // если условие выхода - неверно, возвращаемся в начало для написания нового сообщения
            }
            // если условие выхода - верно, выходим из цикла и завершаем работу потока
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }

    public void sendMessageToChat(BufferedWriter bufferedWriter, String jsonText) {
        try  {
            bufferedWriter.write(jsonText);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }
}





