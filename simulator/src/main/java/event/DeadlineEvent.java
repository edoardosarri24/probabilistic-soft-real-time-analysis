package event;

import java.time.Duration;
import taskSet.Job;

public final class DeadlineEvent extends Event {

    private final Job job;

    public DeadlineEvent(Duration time, Job job) {
        super(time);
        this.job = job;
    }

    public Job getJob() {
        return this.job;
    }

}
