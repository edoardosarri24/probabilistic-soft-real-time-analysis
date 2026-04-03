package utils.collector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskSet.Task;

public final class TaskExecutionTimeCollector {

    private final Map<Task, List<Duration>> taskExecutionTime = new HashMap<>();

    // Methods
    public void add(Task task, Duration executionTime) {
        this.taskExecutionTime
            .computeIfAbsent(task, k -> new ArrayList<>())
            .add(executionTime);
    }

    public void clear() {
        this.taskExecutionTime.clear();
    }

    public Map<Task, List<Duration>> getTaskExecutionTime() {
        return java.util.Collections.unmodifiableMap(this.taskExecutionTime);
    }

}
