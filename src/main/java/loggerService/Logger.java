package loggerService;

import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
    protected final FileWriter fileWriter;
    protected final SimpleDateFormat simpleDateFormat;

    public Logger(FileWriter fileWriter, SimpleDateFormat simpleDateFormat) throws IOException {
        this. fileWriter = fileWriter;
        this.simpleDateFormat = simpleDateFormat;
    }

    public void close() throws IOException {
        fileWriter.close();
    }

    public void log(String message) {
        if (!StringUtils.isBlank(message)) {
            String currentTime = simpleDateFormat.format(Calendar.getInstance().getTime());
            String messageForLogging = "[" + currentTime + "] " + message + "\n";
            try {
                fileWriter.write(messageForLogging);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
