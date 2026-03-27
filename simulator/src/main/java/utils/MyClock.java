package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

/**
 * Represents the global clock of the system.
 * <p>
 * This class provides methods to access and manipulate the current time,
 * allowing to advance the clock to a specific time or by a given duration.
 */
public final class MyClock {

    private Duration currentTime;

    // Constructor.
    public MyClock() {
        this.currentTime = Duration.ZERO;
    }

    // Getter and setter.
    public Duration getCurrentTime() {
        return this.currentTime;
    }

    // Methods.
    public void advanceTo(Duration newTime) {
        if (newTime.compareTo(currentTime) < 0)
            throw new IllegalArgumentException("Cannot advance to a past time");
        this.currentTime = newTime;
    }

    public void advanceBy(Duration delta) {
        if (delta.isNegative())
            throw new IllegalArgumentException("Cannot advance by negative duration");
        this.currentTime = this.currentTime.plus(delta);
    }

    /**
     * @return A string representation of the current time in milliseconds, rounded to three decimal places
     */
    public String printCurrentTime() {
        long nanos = this.currentTime.toNanos();
        BigDecimal millis = new BigDecimal(nanos).divide(BigDecimal.TEN.pow(6), 3, RoundingMode.HALF_UP);
        return "" + millis;
    }

}
