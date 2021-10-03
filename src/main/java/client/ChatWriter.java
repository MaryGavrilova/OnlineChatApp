package client;

import messageService.Message;
import messageService.MessageJsonConverter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static client.Client.COMMAND_TO_EXIT;
import static client.Client.LOGGER;

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
            System.out.println("Введите текст сообщения:");
            try (Scanner scanner = new Scanner(System.in)) {
                String inputMessageText = scanner.nextLine();

                // создаем объект класса Message
                Message message = new Message(chatParticipantName, inputMessageText);
                System.out.println(Thread.currentThread().getName() + ": " + message);
                LOGGER.log(message.toString());

                // конвертируем объект класса Message в JSON file
                MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                String jsonText = messageJsonConverter.convertMessageToJson(message);

                // отправляем сообщение серверу
                sendMessageToChat(socket, jsonText);

                // проверяем условия продолжения работы потока
                if (message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                    System.out.println(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    LOGGER.log(Thread.currentThread().getName() + ": Сlient initiated connection closure");
                    break;
                }
            }
            // если условие выхода - неверно, возвращаемся в начало для написания нового сообщения
        }
        // если условие выхода - верно, выходим из цикла и завершаем работу потока
    }

    public static void sendMessageToChat(Socket socket, String jsonText) {
        // открываем канал записи в сокет
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            bw.write(jsonText);
            bw.flush();
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + ": " + e.getMessage());
            LOGGER.log(Thread.currentThread().getName() + ": " + e.getMessage());
        }
    }
}





