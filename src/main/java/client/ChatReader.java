package client;

import loggerService.Logger;
import messageService.Message;
import messageService.MessageJsonConverter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static client.Client.*;

public class ChatReader extends Thread {
    protected Socket socket;
    protected String chatParticipantName;
    protected Logger logger;
    protected Scanner scanner;

    public ChatReader(ThreadGroup group, String name, Socket socket, String chatParticipantName, Logger logger, Scanner scanner) {
        super(group, name);
        this.socket = socket;
        this.chatParticipantName = chatParticipantName;
        this.logger = logger;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        try {
            // открываем поток чтения
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (!socket.isClosed()) {

                // проверяем поступило ли сообщение от сервера
                if (bufferedReader.ready()) {

                    // получаем сообщение от сервера
                    String jsonText = receiveMessage(bufferedReader);
                    logger.log(Thread.currentThread().getName() + ": message is read from chat");

                    if (!(jsonText == null)) {
                        //парсим json строку в объект класса Message
                        MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                        Message message = messageJsonConverter.parseJsonToMessage(jsonText);

                        // записываем полученное из чата сообщение с указанием имени пользователя и времени отправки в файл file.log
                        logger.log(message.toString());

                        // печатаем сообщение в консоль
                        System.out.println(message);

                        // проверяем условия продолжения работы потока
                        if (message.getSenderName().equals(chatParticipantName) & message.getMessageText().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                            logger.log(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                            break;
                        }
                    }
                }
                // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
            }
            // если условие выхода - верно, выходим из цикла и завершаем работу потока
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }

    public String receiveMessage(BufferedReader bufferedReader) {
        String jsonText = null;
        try {
            jsonText = bufferedReader.readLine();
            return jsonText;
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
        return jsonText;
    }
}

