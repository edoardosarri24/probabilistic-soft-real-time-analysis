package event;

import java.time.Duration;
import utils.MyUtils;

public abstract class Event implements Comparable<Event> {

    private final Duration time;

    public Event(Duration time) {
        this.time = MyUtils.requireNonNegative(time, "time");
    }

    public Duration getTime() {
        return this.time;
    }

    @Override
    public int compareTo(Event other) {
        return this.time.compareTo(other.time);
    }

}
