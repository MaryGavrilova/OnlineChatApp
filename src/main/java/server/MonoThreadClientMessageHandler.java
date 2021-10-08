package server;

import loggerService.Logger;
import messageService.Message;
import messageService.MessageJsonConverter;

import java.io.*;
import java.util.List;

import static server.MultiThreadChatServer.*;

public class MonoThreadClientMessageHandler implements Runnable {

    protected ChatParticipant chatParticipant;
    protected Logger logger;
    protected List<ChatParticipant> chatParticipantsList;

    public MonoThreadClientMessageHandler(ChatParticipant chatParticipant, Logger logger, List<ChatParticipant> chatParticipantsList) {
        this.chatParticipant = chatParticipant;
        this.logger = logger;
        this.chatParticipantsList = chatParticipantsList;
    }

    @Override
    public void run() {
        try {
            System.out.println("Server is processing messages from " + chatParticipant);
            logger.log("Server is processing messages from " + chatParticipant.toString());
            while (chatParticipant.isConnected()) {
                // проверяем поступило ли сообщение от клиента
                if (chatParticipant.isReadyToBeRead()) {
                    System.out.println("Read message from " + chatParticipant);
                    logger.log("Read message from " + chatParticipant.toString());
                    //получаем сообщение в виде json строки
                    String jsonText = receiveMessage();

                    if (!(jsonText == null)) {
                        //парсим json строку в объект класса Message
                        MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                        Message message = messageJsonConverter.parseJsonToMessage(jsonText);

                        // отправляем сообщение в общий чат путем рассылки каждому участнику
                        sendMessageToChat(jsonText);

                        // записываем отправленное в чат сообщение с указанием имени пользователя и времени отправки в файл логирования
                        System.out.println("Chat:\n" + message);
                        logger.log(message.toString());

                        // проверяем условия продолжения работы с клиентом
                        if (message.getMessage().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                            System.out.println(chatParticipant + " initiated connection closure");
                            logger.log(chatParticipant.toString() + " initiated connection closure");
                            break;
                        }
                    }
                }
                // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
            }

        // если условие выхода - верно, удаляем клиента из списка участников чата
            if (chatParticipantsList.remove(chatParticipant)) {
                 System.out.println(chatParticipant + " is deleted from chat participants' list");
                logger.log(chatParticipant.toString() + " is deleted from chat participants' list");
            } else {
                System.out.println(chatParticipant + " is not found in chat participants' list");
                logger.log(chatParticipant.toString() + " is not found in chat participants' list");
            }
            // отключаем клиента через закрытие сокета
            chatParticipant.disconnect();
            System.out.println(chatParticipant + " disconnected");
            logger.log(chatParticipant.toString() + " disconnected");

        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }

    public String receiveMessage() throws IOException {
        return chatParticipant.getBufferedReader().readLine();
    }

    public void sendMessageToChat(String jsonText) {
        for (ChatParticipant currentChatParticipant : chatParticipantsList) {
            try {
                System.out.println("Message is sent from " + chatParticipant.toString()
                        + " to " + currentChatParticipant.toString());
                logger.log("Message is sent " + chatParticipant.toString()
                        + " to " + currentChatParticipant.toString());
                currentChatParticipant.getBufferedWriter().write(jsonText);
                currentChatParticipant.getBufferedWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
            }
        }
    }
}


