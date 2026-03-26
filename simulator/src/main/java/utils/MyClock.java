package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

/**
 * Represents the global clock of the system.
 * <p>
 * This singleton class provides methods to access and manipulate the current global time,
 * allowing to advance the clock to a specific time or by a given duration.
 */
public final class MyClock {

    private static MyClock INSTANCE = new MyClock();
    private Duration currentTime = Duration.ZERO;

    private MyClock() {}

    public static MyClock reset() {
        INSTANCE = new MyClock();
        return INSTANCE;
    }

    public static Duration getCurrentTime() {
        return INSTANCE.currentTime;
    }

    public static void advanceTo(Duration newTime) {
        INSTANCE.currentTime = newTime;
    }

    public static void advanceBy(Duration delta) {
        if (delta.isNegative())
            throw new IllegalArgumentException("Cannot advance by negative duration");
        INSTANCE.currentTime = INSTANCE.currentTime.plus(delta);
    }

    /**
    *Returns the current time as a string representing milliseconds with three decimal places.
    * @return A string representation of the current time in milliseconds, rounded to three decimal places
    */
    public static String printCurrentTime() {
        long nanos = INSTANCE.currentTime.toNanos();
        BigDecimal millis = new BigDecimal(nanos).divide(BigDecimal.TEN.pow(6), 3, RoundingMode.HALF_UP);
        return "" + millis;
    }

}