package client;

import messageService.Message;
import messageService.MessageJsonConverter;

import java.io.*;
import java.net.Socket;

import static client.Client.*;

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
        while (!socket.isClosed()) {

            // получаем сообщение от сервера
            String jsonText = receiveMessage(socket);
            if (!(jsonText == null)) {
                //парсим json строку в объект класса Message
                MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                Message message = messageJsonConverter.parseJsonToMessage(jsonText);

                // печатаем сообщение в консоль
                System.out.println(message);

                // записываем полученное из чата сообщение с указанием имени пользователя и времени отправки в файл file.log
                LOGGER.log(message.toString());

                // проверяем условия продолжения работы потока
                if (message.getSenderName().equals(chatParticipantName) & message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                    System.out.println(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    LOGGER.log(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    break;
                }
            }
            // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
        }
        // если условие выхода - верно, выходим из цикла и завершаем работу потока
    }

    public String receiveMessage(Socket socket) {
        String jsonText = null;
        // открываем канал чтения из сокета
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            if (br.ready()) {
                jsonText = br.readLine();
                return jsonText;
            }
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + ": " + e.getMessage());
            LOGGER.log(Thread.currentThread().getName() + ": " + e.getMessage());
        }
        return jsonText;
    }
}

