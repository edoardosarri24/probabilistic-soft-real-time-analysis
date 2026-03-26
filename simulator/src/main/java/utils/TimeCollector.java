package utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import taskSet.Task;

public class TimeCollector {

    private Map<Task, Duration> jobExecutionTime;

    public TimeCollector() {
        this.jobExecutionTime = new HashMap<>();
    }

    public void addTime(Task task, Duration time) {
        this.jobExecutionTime.put(task, time);
    }

}