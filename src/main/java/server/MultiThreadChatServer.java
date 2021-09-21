package server;

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
    // создаем список всех подключившихся участников чата
    public static List<Socket> chatParticipantsList = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        // устанавливаем порт для подключения клиентов через файл настроек
        int port = readServerSettingsFile();
        if (port > 1 & port < 65535) {
            System.out.println("Port tuned in");
        } else {
            System.out.println("Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        // создаем файл для записи всех отправленных через сервер сообщений, если он еще не создан
        createLogFile();

        // создаем пул потоков, куда будем класть задачи по обработке сообщений от клиентов
        ExecutorService serverExecutorService = Executors.newFixedThreadPool(MAX_NUMBER_OF_CLIENTS);

        // стартуем сервер на заданном порту
        try (ServerSocket server = new ServerSocket(port);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {

                // проверяем поступили ли команды из консоли сервера
                if (br.ready()) {
                    System.out.println("Main Server found message in channel");

                    // если команда /exit - инициализируем закрытие сервера и
                    // выход из цикла создания монопоточных серверов
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase(COMMAND_TO_EXIT)) {
                        System.out.println("Main Server initiate exiting");
                        server.close();
                        break;
                    }
                }

                // если команд нет - ждем подключения клиента
                Socket client = server.accept();
                System.out.print("Connection accepted.");
                // записываем клиента в список рассылки общего чата
                chatParticipantsList.add(client);
                System.out.println("New client added to chat participants' list");
                //для обработки сообщений от клиента отправляем его в отдельный поток пула
                serverExecutorService.execute(new MonoThreadClientMessageHandler(client));
                System.out.print("Client handed over to separate thread");
            }

            // закрытие пула после завершения работы всех потоков, обрабатывающих сообщения
            serverExecutorService.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int readServerSettingsFile() {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(NAME_OF_SETTINGS_FILE))) {
            String s = br.readLine();
            port = Integer.parseInt(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    public static void createLogFile() {
        File logFile = new File(NAME_OF_LOG_FILE);
        try {
            if (logFile.createNewFile()) {
                System.out.println("File " + NAME_OF_LOG_FILE + " created");
            } else {
                System.out.println("File " + NAME_OF_LOG_FILE + " already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
