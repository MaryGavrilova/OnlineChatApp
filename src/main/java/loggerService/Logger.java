package loggerService;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
    protected String logFileName;

    public Logger(String logFileName) {
        this.logFileName = logFileName;
    }

    public void log(String message) {
        String currentTime = new SimpleDateFormat("yyyy:MM:dd  HH:mm:ss").format(Calendar.getInstance().getTime());
        String messageForLogging = "[" + currentTime + "] " + message + "\n";
        try (FileWriter file = new FileWriter(logFileName, true)) {
            file.write(messageForLogging);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
