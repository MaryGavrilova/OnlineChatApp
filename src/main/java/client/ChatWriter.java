package client;

import loggerService.Logger;
import messageService.Message;
import messageService.MessageBuilder;
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
                Message message;
                try {
                    message = new MessageBuilder()
                            .setSenderName(chatParticipantName)
                            .setMessageText(messageText)
                            .build();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
                    continue;
                }
                logger.log(Thread.currentThread().getName() + ": message is prepared");

                // конвертируем объект класса Message в JSON file
                MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                String jsonText = messageJsonConverter.convertMessageToJson(message);
                logger.log(Thread.currentThread().getName() + ": message is converted: " + jsonText);

                // отправляем сообщение серверу
                sendMessage(bufferedWriter, jsonText);
                logger.log(Thread.currentThread().getName() + ": message is sent to chat");
                logger.log(message.toString());

                // проверяем условия продолжения работы потока
                if (message.getMessageText().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                    logger.log(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    break;
                }
                // если условие выхода - неверно, возвращаемся в начало для написания нового сообщения
            }
            // если условие выхода - верно, выходим из цикла и завершаем работу потока
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }

    public void sendMessage(BufferedWriter bufferedWriter, String jsonText) {
        try {
            bufferedWriter.write(jsonText);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }
}





