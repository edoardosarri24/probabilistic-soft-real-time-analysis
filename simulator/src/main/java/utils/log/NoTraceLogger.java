package utils.log;

/**
 * A logger implementation that does nothing.
 * Used as a default when no logger is provided to the scheduler.
 */
public final class NoTraceLogger implements MyTraceLogger {
    @Override
    public void log(String message) {
        // Do nothing
    }

    @Override
    public void close() {
        // Do nothing
    }
}
