package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final String NAME_OF_SETTINGS_FILE = "clientSettings.txt";
    public static final String NAME_OF_LOG_FILE = "clientFile.log";
    public static final String COMMAND_TO_EXIT = "/exit";
    public static Socket socket;

    public static void main(String[] args) {
        String clientName = null;

        // читаем настройки приложения из файла настроек для определения порта
        int port = readClientSettingsFile();
        if (port > 1 & port < 65535) {
            System.out.println("Port tuned in");
        } else {
            System.out.println("Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        // создаем файл для записи всех полученных из чата сообщений, если он еще не создан
        createLogFile();

        // создаём сокет общения на стороне клиента
        try {
            socket = new Socket(HOST, port);
            System.out.println("Client connected to socket");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // пользователь выбирает имя для участия в чате
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите имя для участия в чате:");
            clientName = scanner.nextLine();
        }

        // запускаем поток отправки сообщений в чат;
        ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, clientName);
        chatWriter.start();

        // запускаем поток чтения сообщений из чата;
        ChatReader chatReader = new ChatReader(null, "ChatReader", socket, clientName);
        chatReader.start();

        // пока один из потоков работает, соединение с сервером остается открытым
        // как только оба потока закончили работу, клиент закрывает соединение
        while (true) {
            if (!chatWriter.isAlive() & !chatReader.isAlive()) {
                try {
                    socket.close();
                    System.out.println("Client disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int readClientSettingsFile() {
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