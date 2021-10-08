package client;

import loggerService.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final String NAME_OF_SETTINGS_FILE = "clientSettings.txt";
    public static final String NAME_OF_LOG_FILE = "clientFile.log";
    public static final String COMMAND_TO_EXIT = "/exit";

    public static void main(String[] args) {
        // создаем логгер для записи ошибок и сообщений
        Logger logger = new Logger(NAME_OF_LOG_FILE);

        // создаем файл для записи всех полученных из чата сообщений, если он еще не создан
        createLogFile();

        // читаем настройки приложения из файла настроек для определения порта
        int port = readClientSettingsFile(logger);
        if (port > 1 & port < 65535) {
            System.out.println("Client: Port " + port + " tuned in");
            logger.log("Client: Port " + port + " tuned in");
        } else {
            System.out.println("Client: Port can not be less that 1 or more than 65535");
            logger.log("Client: Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        try {
            // создаём сокет общения на стороне клиента
            Socket socket = new Socket(HOST, port);
            System.out.println("Client: connected");
            logger.log("Client: connected");

            // создаем сканнер для считывания сообщений с консоли
            Scanner scanner = new Scanner(System.in);

            // пользователь выбирает имя для участия в чате
            System.out.println("Введите имя для участия в чате:");
            String chatParticipantName = scanner.nextLine();
            System.out.println("Client: participant name is listed as " + chatParticipantName);
            logger.log("Client: participant name is listed as " + chatParticipantName);

            // создаем поток отправки сообщений в чат;
            ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, chatParticipantName, logger, scanner);

            // создаем поток чтения сообщений из чата;
            ChatReader chatReader = new ChatReader(null, "ChatReader", socket, chatParticipantName, logger, scanner);

            // запускаем потоки
            chatWriter.start();
            chatReader.start();
            System.out.println("Client: ChatWriter and ChatReader started");
            logger.log("Client: ChatWriter and ChatReader started");

            // ждем завершения работы потоков чтения и отправки для закрытия сокета
            System.out.println("Client: waiting for ChatWriter and ChatReader shutdown");
            logger.log("Client: waiting for ChatWriter and ChatReader shutdown");
            chatWriter.join();
            chatReader.join();

            // закрываем сокет общения на стороне клиента после завершения работы потоков чтения и записи
            socket.close();
            System.out.println("Client: disconnected");
            logger.log("Client: disconnected");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            logger.log("Client exception: " + e.getMessage());
        }
    }

    public static int readClientSettingsFile(Logger logger) {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(NAME_OF_SETTINGS_FILE))) {
            String s = br.readLine();
            port = Integer.parseInt(s);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Client exception: " + e.getMessage());
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