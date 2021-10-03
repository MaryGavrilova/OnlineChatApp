package server;

import messageService.Message;
import messageService.MessageJsonConverter;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static server.MultiThreadChatServer.*;

public class MonoThreadClientMessageHandler implements Runnable {

    protected Socket client;

    public MonoThreadClientMessageHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!client.isClosed()) {
            System.out.println("Server is processing message from client " + client.toString());
            LOGGER.log("Server is processing message from client " + client.toString());

            //получаем сообщение в виде json строки
            String jsonText = receiveMessage(client);

            //парсим json строку в объект класса Message
            MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
            Message message = messageJsonConverter.parseJsonToMessage(jsonText);

            // отправляем сообщение в общий чат путем рассылки каждому участнику
            sendMessageToChat(chatParticipantsList, jsonText);

            // записываем отправленное в чат сообщение с указанием имени пользователя и времени отправки в файл логирования
            System.out.println("Message from " + client.toString() + ": " + message);
            LOGGER.log(message.toString());

            // проверяем условия продолжения работы с клиентом
            if (message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                System.out.println("Сlient " + client.toString() + " initiated connection closure");
                LOGGER.log("Сlient " + client.toString() + " initiated connection closure");
                break;
            }
            // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
        }

        // если условие выхода - верно, удаляем клиента из списка участников чата и  закрываем сокет общения с клиентом
        try {
            if (chatParticipantsList.remove(client)) {
                System.out.println("Client " + client.toString() + " is deleted from chat participants' list");
                LOGGER.log("Client " + client.toString() + " is deleted from chat participants' list");
            } else {
                System.out.println("Client " + client.toString() +" is not found in chat participants' list");
                LOGGER.log("Client " + client.toString() +" is not found in chat participants' list");
            }
            client.close();
            System.out.println("Client " + client.toString() + " disconnected");
            LOGGER.log("Client " + client.toString() + " disconnected");
        } catch (IOException e) {
            System.out.print(Thread.currentThread().getName() + ": " + e.getMessage());
            LOGGER.log(Thread.currentThread().getName() + ": " + e.getMessage());
        }
    }

    public String receiveMessage(Socket client) {
        String jsonText = null;
        // открываем канал чтения из сокета
        try (BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            System.out.println("BufferedReader created to read messages from " + client.toString());
            LOGGER.log("BufferedReader created to read messages from " + client.toString());
            jsonText = br.readLine();
        } catch (IOException e) {
            System.out.print(Thread.currentThread().getName() + ": " + e.getMessage());
            LOGGER.log(Thread.currentThread().getName() + ": " + e.getMessage());
        }
        return jsonText;
    }

    public void sendMessageToChat(List<Socket> chatParticipantsList, String jsonText) {
        for (int i = 0; i < chatParticipantsList.size(); i++) {
            Socket chatParticipant = chatParticipantsList.get(i);
            // открываем канал записи в сокет
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(chatParticipant.getOutputStream()))) {
                System.out.println("BufferedWriter created for client " + client.toString()
                        + " to send message to " + chatParticipant.toString());
                LOGGER.log("BufferedWriter created for client " + client.toString()
                        + " to send message to " + chatParticipant.toString());
                bw.write(jsonText);
                bw.flush();
            } catch (IOException e) {
                System.out.print(Thread.currentThread().getName() + ": " + e.getMessage());
                LOGGER.log(Thread.currentThread().getName() + ": " + e.getMessage());
            }
        }
    }
}


