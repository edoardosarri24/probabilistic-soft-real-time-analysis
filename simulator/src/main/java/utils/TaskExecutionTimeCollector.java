package utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskSet.Task;

public final class TaskExecutionTimeCollector {

    private Map<Task, List<Duration>> taskExecutionTime = new HashMap<>();

    // Methods
    public void add(Task task, Duration executionTime) {
        this.taskExecutionTime
            .computeIfAbsent(task, k -> new java.util.ArrayList<>())
            .add(executionTime);
    }

    public void clear() {
        this.taskExecutionTime.clear();
    }

}
