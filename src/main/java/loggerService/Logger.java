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
        if (!(message == null)) {
            String currentTime = new SimpleDateFormat("yyyy:MM:dd  HH:mm:ss").format(Calendar.getInstance().getTime());
            String messageForLogging = "[" + currentTime + "] " + message + "\n";
            try (FileWriter fileWriter = new FileWriter(logFileName, true)) {
                fileWriter.write(messageForLogging);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
