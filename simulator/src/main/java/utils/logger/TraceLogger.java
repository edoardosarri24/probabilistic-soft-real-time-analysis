package utils.logger;

public interface TraceLogger {
    void log(String message);
    void warning(String message);
    void close();
}
