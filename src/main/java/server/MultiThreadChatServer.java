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
    // создаем логгер для записи ошибок и сообщений от клиентов
    public static final Logger LOGGER = new Logger(NAME_OF_LOG_FILE);
    // создаем список всех подключившихся участников чата
    public static List<Socket> chatParticipantsList = new CopyOnWriteArrayList<>();


    public static void main(String[] args) {
        // создаем лог файл, если он еще не создан
        createLogFile();

        // устанавливаем порт для подключения клиентов через файл настроек
        int port = readServerSettingsFile();
        if (port > 1 & port < 65535) {
            System.out.println("Main Server: Port tuned in");
            LOGGER.log("Main Server: Port tuned in");
        } else {
            System.out.println("Main Server: Port can not be less that 1 or more than 65535");
            LOGGER.log("Main Server: Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        // создаем пул потоков, куда будем класть задачи по обработке сообщений от клиентов
        ExecutorService serverExecutorService = Executors.newFixedThreadPool(MAX_NUMBER_OF_CLIENTS);
        System.out.println("Main Server: ExecutorService created");
        LOGGER.log("Main Server: ExecutorService created");

        // стартуем сервер на заданном порту
        try (ServerSocket server = new ServerSocket(port);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Main Server: started");
            LOGGER.log("Main Server: started");

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {

                // проверяем поступили ли команды из консоли сервера
                if (br.ready()) {
                    System.out.println("Main Server: Message is found in channel");
                    LOGGER.log("Main Server: Message is found in channel");

                    // если команда /exit - инициализируем закрытие сервера и
                    // выход из цикла создания монопоточных серверов
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase(COMMAND_TO_EXIT)) {
                        System.out.println("Main Server: initiated exiting");
                        LOGGER.log("Main Server: initiated exiting");
                        server.close();
                        break;
                    }
                }

                // если команд нет - ждем подключения клиента
                Socket client = server.accept();
                System.out.println("Main Server: Connection with " + client.toString() + " accepted");
                LOGGER.log("Main Server: Connection with " + client.toString() + " accepted");

                // записываем клиента в список рассылки общего чата
                chatParticipantsList.add(client);
                System.out.println("Main Server: New client " + client.toString() + " added to chat participants' list");
                LOGGER.log("Main Server: New client " + client.toString() + " added to chat participants' list");

                //для обработки сообщений от клиента отправляем его в отдельный поток пула
                serverExecutorService.execute(new MonoThreadClientMessageHandler(client));
                System.out.println("Main Server: Client " + client.toString() + " handed over to separate thread");
                LOGGER.log("Main Server: Client " + client.toString() + " handed over to separate thread");
            }

            // закрытие пула после завершения работы всех потоков, обрабатывающих сообщения
            serverExecutorService.shutdown();
            System.out.println("Main Server: ExecutorService shut down");
            LOGGER.log("Main Server: ExecutorService shut down");

        } catch (IOException e) {
            System.out.println("Main Server: " + e.getMessage());
            LOGGER.log("Main Server: " + e.getMessage());
        }
    }

    public static int readServerSettingsFile() {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(NAME_OF_SETTINGS_FILE))) {
            String s = br.readLine();
            port = Integer.parseInt(s);
        } catch (IOException e) {
            System.out.println("Main Server: " + e.getMessage());
            LOGGER.log("Main Server: " + e.getMessage());
        }
        return port;
    }

    public static void createLogFile() {
        File logFile = new File(NAME_OF_LOG_FILE);
        try {
            if (logFile.createNewFile()) {
                System.out.println("Main Server: File " + NAME_OF_LOG_FILE + " created");
                LOGGER.log("Main Server: File " + NAME_OF_LOG_FILE + " created");
            } else {
                System.out.println("Main Server: File " + NAME_OF_LOG_FILE + " already exists");
                LOGGER.log("Main Server: File " + NAME_OF_LOG_FILE + " already exists");
            }
        } catch (IOException e) {
            System.out.println("Main Server: " + e.getMessage());
        }
    }
}
