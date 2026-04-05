package utils.collector;

import java.util.HashMap;
import java.util.Map;
import taskSet.Task;

public final class DeadlineMissCollector {

    private final Map<Task, Integer> deadlineMissCount = new HashMap<>();
    private final Map<Task, Integer> jobPerTaskReleased = new HashMap<>();

    // Methods
    public void incrementDeadlineMissCount(Task task) {
        this.deadlineMissCount.merge(task, 1, Integer::sum);
    }

    public void incrementJobPerTaskReleased(Task task) {
        this.jobPerTaskReleased.merge(task, 1, Integer::sum);
    }

    public int getDeadlineMissCount(Task task) {
        return this.deadlineMissCount.getOrDefault(task, 0);
    }

    public int getJobPerTaskReleased(Task task) {
        return this.jobPerTaskReleased.getOrDefault(task, 0);
    }

    public void clear() {
        this.deadlineMissCount.clear();
        this.jobPerTaskReleased.clear();
    }

}
