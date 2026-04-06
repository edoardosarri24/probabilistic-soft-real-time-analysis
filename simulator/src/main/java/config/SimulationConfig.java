package config;

import java.util.List;
import taskSet.Task;
import taskSet.TaskSet;

public final class SimulationConfig {

    public double simulationDurationMs;
    public StrategyConfig deadlineMissStrategy;
    public List<TaskConfig> tasks;

    public TaskSet toTaskSet() {
        Task[] taskArray = tasks.stream()
            .map(TaskConfig::toTask)
            .toArray(Task[]::new);
        return new TaskSet(taskArray);
    }

}
