package client;

import loggerService.Logger;
import org.apache.commons.lang3.StringUtils;

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

        // читаем настройки приложения из файла настроек для определения порта
        int port = readClientSettingsFile(logger, new File(NAME_OF_SETTINGS_FILE));
        if (port > 1 & port < 65535) {
            logger.log("Client: Port " + port + " tuned in");
        } else {
            logger.log("Client: Port can not be less that 1 or more than 65535");
            System.exit(0);
        }

        try {
            // создаём сокет общения на стороне клиента
            Socket socket = new Socket(HOST, port);
            logger.log("Client: connected");

            // создаем сканнер для считывания сообщений с консоли
            Scanner scanner = new Scanner(System.in);

            // пользователь выбирает имя для участия в чате
            String chatParticipantName;
            while (true) {
                System.out.println("Введите имя для участия в чате:");
                chatParticipantName = scanner.nextLine();
                if (!StringUtils.isBlank(chatParticipantName)) {
                    break;
                }
                System.out.println("Имя введено некорретно, попробуйте еще раз.");
            }
            logger.log("Client: participant name is listed as " + chatParticipantName);

            // создаем поток отправки сообщений в чат;
            ChatWriter chatWriter = new ChatWriter(null, "ChatWriter", socket, chatParticipantName, logger, scanner);

            // создаем поток чтения сообщений из чата;
            ChatReader chatReader = new ChatReader(null, "ChatReader", socket, chatParticipantName, logger, scanner);

            // запускаем потоки
            chatWriter.start();
            chatReader.start();
            logger.log("Client: ChatWriter and ChatReader started");

            // ждем завершения работы потоков чтения и отправки для закрытия сокета
            logger.log("Client: waiting for ChatWriter and ChatReader shutdown");
            chatWriter.join();
            chatReader.join();

            // закрываем сокет общения на стороне клиента после завершения работы потоков чтения и записи
            socket.close();
            logger.log("Client: disconnected");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            logger.log("Client exception: " + e.getMessage());
        }
    }

    public static int readClientSettingsFile(Logger logger, File file) {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String data = br.readLine();
            port = Integer.parseInt(data);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            logger.log("Client exception: " + e.getMessage());
        }
        return port;
    }
}