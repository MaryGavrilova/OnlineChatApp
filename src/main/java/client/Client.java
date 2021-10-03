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
    // создаем логгер для записи ошибок и сообщений
    public static final Logger LOGGER = new Logger(NAME_OF_LOG_FILE);
    public static Socket socket;

    public static void main(String[] args) {
        // создаем файл для записи всех полученных из чата сообщений, если он еще не создан
        createLogFile();

        // читаем настройки приложения из файла настроек для определения порта
        int port = readClientSettingsFile();
        if (port > 1 & port < 65535) {
            System.out.println("Client: Port tuned in");
            LOGGER.log("Client: Port tuned in");
        } else {
            System.out.println("Client: Port can not be less that 1 or more than 65535");
            LOGGER.log("Client: Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        // создаём сокет общения на стороне клиента
        try {
            socket = new Socket(HOST, port);
            System.out.println("Client: connected to socket");
            LOGGER.log("Client: connected to socket");
        } catch (Exception e) {
            System.out.println("Client: " + e.getMessage());
            LOGGER.log("Client: " + e.getMessage());
        }

        // пользователь выбирает имя для участия в чате
        String clientName;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите имя для участия в чате:");
            clientName = scanner.nextLine();
            System.out.println("Client: participant name is listed as " + clientName);
            LOGGER.log("Client: participant name is listed as " + clientName);
        }
        try {
            // запускаем поток отправки сообщений в чат;
            ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, clientName);
            chatWriter.start();
            System.out.println("Client: ChatWriter started");
            LOGGER.log("Client: ChatWriter started");

            // запускаем поток чтения сообщений из чата;
            ChatReader chatReader = new ChatReader(null, "ChatReader", socket, clientName);
            chatReader.start();
            System.out.println("Client: ChatReader started");
            LOGGER.log("Client: ChatReader started");

            // ждем завершения работы потоков чтения и отправки для закрытия сокета
            System.out.println("Client: waiting for ChatWriter and ChatReader shutdown");
            LOGGER.log("Client: waiting for ChatWriter and ChatReader shutdown");
            chatWriter.join();
            chatReader.join();

            socket.close();
            System.out.println("Client: disconnected");
            LOGGER.log("Client: disconnected");
        } catch (InterruptedException | IOException e) {
            System.out.println("Client: " + e.getMessage());
            LOGGER.log("Client: " + e.getMessage());
        }
    }

    public static int readClientSettingsFile() {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(NAME_OF_SETTINGS_FILE))) {
            String s = br.readLine();
            port = Integer.parseInt(s);
        } catch (IOException e) {
            System.out.println("Client: " + e.getMessage());
            LOGGER.log("Client: " + e.getMessage());
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
            System.out.println("Client: " + e.getMessage());
        }
    }
}