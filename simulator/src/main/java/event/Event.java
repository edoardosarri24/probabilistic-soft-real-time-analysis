package event;

import java.time.Duration;

public abstract class Event implements Comparable<Event> {

    private final Duration time;

    public Event(Duration time) {
        this.time = time;
    }

    public Duration getTime() {
        return this.time;
    }

    @Override
    public int compareTo(Event object) {
        // The importantest event is the one that arrives first.
        int timeCompare = this.time.compareTo(object.time);
        if (timeCompare != 0)
            return timeCompare;
        // After is more important a Deadline event on a Release event.
        if (this instanceof DeadlineEvent && object instanceof ReleaseEvent)
            return -1;
        if (this instanceof ReleaseEvent && object instanceof DeadlineEvent)
            return 1;
        return 0;
    }
}
