package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.*;

public final class TraceLogger {

    private final Logger logger;
    private FileHandler fileHandler;
    private Consumer<String> customLogger;

    // Constructor
    public TraceLogger() {
        this("results/trace.log");
    }

    public TraceLogger(String fileName) {
        this.logger = Logger.getLogger(TraceLogger.class.getName() + "-" + fileName + "-" + System.currentTimeMillis());
        try {
            // Ensure parent directory exists
            Path logPath = Paths.get(fileName);
            if (logPath.getParent() != null)
                Files.createDirectories(logPath.getParent());
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
            System.err.println("Error initializing TraceLogger: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Methods
    public void setCustomLogger(Consumer<String> customLogger) {
        this.customLogger = customLogger;
    }

    public void log(String message) {
        this.logger.info(message);
        if (customLogger != null)
            customLogger.accept(message);
    }

    public void close() {
        if (Objects.nonNull(fileHandler)) {
            fileHandler.close();
            logger.removeHandler(fileHandler);
        }
    }

}
