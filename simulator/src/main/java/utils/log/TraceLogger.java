package utils.log;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.*;

public final class TraceLogger implements MyLogger {

    private final Logger logger;
    private FileHandler fileHandler;

    // Constructor
    public TraceLogger() {
        this("trace.log");
    }

    public TraceLogger(String fileName) {
        this.logger = Logger.getLogger(TraceLogger.class.getName() + "-" + fileName);
        try {
            this.fileHandler = new FileHandler(fileName, false);
            this.fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(
                        "[%s] %s%n",
                        record.getLevel().getName(),
                        record.getMessage());
                }
            });
            this.logger.addHandler(fileHandler);
            this.logger.setLevel(Level.ALL);
            this.logger.setUseParentHandlers(false); // Doesn't send logs to console.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methods
    @Override
    public void log(String message) {
        this.logger.info(message);
    }

    @Override
    public void close() {
        if (Objects.nonNull(fileHandler)) {
            fileHandler.close();
            logger.removeHandler(fileHandler);
        }
    }

}
