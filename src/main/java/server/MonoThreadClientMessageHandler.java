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
            logger.log("Server is processing messages from " + chatParticipant.toString());

            while (chatParticipant.isConnected()) {

                // проверяем поступило ли сообщение от клиента
                if (chatParticipant.isReadyToBeRead()) {
                    logger.log("Read message from " + chatParticipant.toString());

                    //получаем сообщение в виде json строки
                    String jsonText = receiveMessage();

                    if (!(jsonText == null)) {
                        //парсим json строку в объект класса Message
                        MessageJsonConverter messageJsonConverter = new MessageJsonConverter();
                        Message message = messageJsonConverter.parseJsonToMessage(jsonText);

                        // отправляем сообщение в общий чат путем рассылки каждому участнику
                        sendMessage(jsonText);
                        logger.log("Message is sent from" + chatParticipant.toString());

                        // записываем отправленное в чат сообщение с указанием имени пользователя и времени отправки в файл логирования
                        logger.log(message.toString());

                        // проверяем условия продолжения работы с клиентом
                        if (message.getMessageText().equalsIgnoreCase(COMMAND_TO_EXIT)) {
                            logger.log(chatParticipant.toString() + " initiated connection closure");
                            break;
                        }
                    }
                }
                // если условие выхода - неверно, возвращаемся в начало для считывания нового сообщения
            }

            // если условие выхода - верно, удаляем клиента из списка участников чата
            if (chatParticipantsList.remove(chatParticipant)) {
                logger.log(chatParticipant.toString() + " is deleted from chat participants' list");
            } else {
                logger.log(chatParticipant.toString() + " is not found in chat participants' list");
            }

            // отключаем клиента через закрытие сокета
            chatParticipant.disconnect();
            logger.log(chatParticipant.toString() + " disconnected");

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
    }

    public String receiveMessage() {
        String jsonText = null;
        try {
            jsonText = chatParticipant.getBufferedReader().readLine();
            return jsonText;
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
        }
        return jsonText;
    }

    public void sendMessage(String jsonText) {
        for (ChatParticipant currentChatParticipant : chatParticipantsList) {
            try {
                currentChatParticipant.getBufferedWriter().write(jsonText);
                currentChatParticipant.getBufferedWriter().newLine();
                currentChatParticipant.getBufferedWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Thread.currentThread().getName() + " exception: " + e.getMessage());
            }
        }
    }
}


