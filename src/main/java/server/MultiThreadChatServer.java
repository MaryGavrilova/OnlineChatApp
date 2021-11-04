package server;

import loggerService.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadChatServer {
    // чтобы на серверной машине хватило ресурсов для общения со множеством клиентов
    // ограничиваем количество возможных подключений
    public static final int MAX_NUMBER_OF_CLIENTS = 4;

    public static final String NAME_OF_SETTINGS_FILE = "serverSettings.txt";
    public static final String NAME_OF_LOG_FILE = "serverFile.log";
    public static final String COMMAND_TO_EXIT = "/exit";

    public static void main(String[] args) {

        // создаем логгер для записи ошибок и сообщений от клиентов
        Logger logger = new Logger(Paths.get(NAME_OF_LOG_FILE));


        // создаем список всех подключившихся участников чата
        List<ChatParticipant> chatParticipantsList = new CopyOnWriteArrayList<>();

        // устанавливаем порт для подключения клиентов через файл настроек
        int port = readServerSettingsFile(logger, Paths.get(NAME_OF_SETTINGS_FILE));
        if (port > 1 & port < 65535) {
            logger.log("Main Server: Port " + port + " tuned in");
        } else {
            logger.log("Main Server: Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        // создаем пул потоков, куда будем класть задачи по обработке сообщений от клиентов
        ExecutorService serverExecutorService = Executors.newFixedThreadPool(MAX_NUMBER_OF_CLIENTS);
        logger.log("Main Server: ExecutorService created");

        // стартуем сервер на заданном порту
        try (ServerSocket server = new ServerSocket(port)) {
            logger.log("Main Server: started");

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {

                // ждем подключения клиента
                Socket client = server.accept();
                ChatParticipant chatParticipant = new ChatParticipant(client);
                logger.log("Main Server: Connection with " + chatParticipant.toString() + " accepted");

                // записываем клиента в список рассылки общего чата
                chatParticipantsList.add(chatParticipant);
                logger.log("Main Server: New client " + chatParticipant.toString() + " added to chat participants' list");

                //для обработки сообщений от клиента отправляем его в отдельный поток пула
                serverExecutorService.execute(new MonoThreadClientMessageHandler(chatParticipant, logger, chatParticipantsList));
                logger.log("Main Server: Client " + chatParticipant.toString() + " handed over to separate thread");
            }

            // закрытие пула после завершения работы всех потоков, обрабатывающих сообщения
            serverExecutorService.shutdown();
            logger.log("Main Server: ExecutorService shut down");

        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Main Server exception: " + e.getMessage());
        }
    }

    public static int readServerSettingsFile(Logger logger, File file) {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s = br.readLine();
            port = Integer.parseInt(s);
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Main Server exception: " + e.getMessage());
        }
        return port;
    }
}
