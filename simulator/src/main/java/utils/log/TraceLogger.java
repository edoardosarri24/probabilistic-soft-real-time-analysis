package utils.log;

public interface TraceLogger {
    void log(String message);
    void close();
}
