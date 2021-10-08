package server;

import loggerService.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        Logger logger = new Logger(NAME_OF_LOG_FILE);

        // создаем лог файл, если он еще не создан
        createLogFile(logger);

        // создаем список всех подключившихся участников чата
        List<ChatParticipant> chatParticipantsList = new CopyOnWriteArrayList<>();

        // устанавливаем порт для подключения клиентов через файл настроек
        int port = readServerSettingsFile(logger);
        if (port > 1 & port < 65535) {
            System.out.println("Main Server: Port " + port + " tuned in");
            logger.log("Main Server: Port " + port + " tuned in");
        } else {
            System.out.println("Main Server: Port can not be less that 1 or more than 65535");
            logger.log("Main Server: Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        // создаем пул потоков, куда будем класть задачи по обработке сообщений от клиентов
        ExecutorService serverExecutorService = Executors.newFixedThreadPool(MAX_NUMBER_OF_CLIENTS);
        System.out.println("Main Server: ExecutorService created");
        logger.log("Main Server: ExecutorService created");

        // стартуем сервер на заданном порту
        try (ServerSocket server = new ServerSocket(port);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Main Server: started");
            logger.log("Main Server: started");

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {

                // проверяем поступили ли команды из консоли сервера
                if (br.ready()) {
                    System.out.println("Main Server: Message is found in channel");
                    logger.log("Main Server: Message is found in channel");

                    // если команда /exit - инициализируем закрытие сервера и
                    // выход из цикла создания монопоточных серверов
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase(COMMAND_TO_EXIT)) {
                        System.out.println("Main Server: initiated exiting");
                        logger.log("Main Server: initiated exiting");
                        server.close();
                        break;
                    }
                }

                // если команд нет - ждем подключения клиента
                Socket client = server.accept();
                ChatParticipant chatParticipant = new ChatParticipant(client);
                System.out.println("Main Server: Connection with " + chatParticipant.toString() + " accepted");
                logger.log("Main Server: Connection with " + chatParticipant.toString() + " accepted");

                // записываем клиента в список рассылки общего чата
                chatParticipantsList.add(chatParticipant);
                System.out.println("Main Server: New client " + chatParticipant.toString() + " added to chat participants' list");
                logger.log("Main Server: New client " + chatParticipant.toString() + " added to chat participants' list");

                //для обработки сообщений от клиента отправляем его в отдельный поток пула
                serverExecutorService.execute(new MonoThreadClientMessageHandler(chatParticipant, logger, chatParticipantsList));
                System.out.println("Main Server: Client " + chatParticipant.toString() + " handed over to separate thread");
                logger.log("Main Server: Client " + chatParticipant.toString() + " handed over to separate thread");
            }

            // закрытие пула после завершения работы всех потоков, обрабатывающих сообщения
            serverExecutorService.shutdown();
            System.out.println("Main Server: ExecutorService shut down");
            logger.log("Main Server: ExecutorService shut down");

        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Main Server exception: " + e.getMessage());
        }
    }

    public static int readServerSettingsFile(Logger logger) {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(NAME_OF_SETTINGS_FILE))) {
            String s = br.readLine();
            port = Integer.parseInt(s);
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Main Server exception: " + e.getMessage());
        }
        return port;
    }

    public static void createLogFile(Logger logger) {
        File logFile = new File(NAME_OF_LOG_FILE);
        try {
            if (logFile.createNewFile()) {
                System.out.println("Main Server: File " + NAME_OF_LOG_FILE + " created");
                logger.log("Main Server: File " + NAME_OF_LOG_FILE + " created");
            } else {
                System.out.println("Main Server: File " + NAME_OF_LOG_FILE + " already exists");
                logger.log("Main Server: File " + NAME_OF_LOG_FILE + " already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Main Server exception: " + e.getMessage());
        }
    }
}
