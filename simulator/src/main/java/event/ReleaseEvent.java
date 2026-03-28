package event;

import java.time.Duration;
import taskSet.Task;
import utils.MyUtils;

public final class ReleaseEvent extends Event {

    private final Task task;

    public ReleaseEvent(Duration time, Task task) {
        super(time);
        this.task = MyUtils.requireNonNull(task, "task");
    }

    public Task getTask() {
        return this.task;
    }

}
