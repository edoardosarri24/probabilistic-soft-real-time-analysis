package event;

import java.time.Duration;
import utils.MyUtils;

public abstract class Event {

    private final Duration time;

    public Event(Duration time) {
        this.time = MyUtils.requireNonNegative(time, "time");
    }

    public Duration getTime() {
        return this.time;
    }

}
