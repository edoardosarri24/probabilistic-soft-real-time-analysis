package utils.logger;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.*;

public final class MyLogger implements TraceLogger {
    private final Logger logger;
    private FileHandler fileHandler;

    public MyLogger() {
        this("trace.log");
    }

    public MyLogger(String fileName) {
        this.logger = Logger.getLogger(MyLogger.class.getName() + "-" + fileName);
        try {
            this.fileHandler = new FileHandler(fileName, false);
            this.fileHandler.setFormatter(new MyFormatter());
            this.logger.addHandler(fileHandler);
            this.logger.setLevel(Level.ALL);
            this.logger.setUseParentHandlers(false); // Doesn't send logs to console.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
