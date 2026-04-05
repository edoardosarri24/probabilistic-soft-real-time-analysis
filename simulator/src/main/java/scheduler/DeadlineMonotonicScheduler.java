package scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import scheduler.deadlineMIssStrategy.DeadlineMissStrategy;
import taskSet.Task;
import taskSet.TaskSet;

public final class DeadlineMonotonicScheduler extends FixedPriorityScheduler {

    // Constructor
    public DeadlineMonotonicScheduler(TaskSet taskSet, double simulationDuration, DeadlineMissStrategy strategy) {
        super(taskSet, simulationDuration, strategy);
    }

    // methods
    @Override
    protected void assignPriority() {
        List<Task> sortedByDeadline = getTaskSet().getTasks().stream()
            .sorted(Comparator.comparing(Task::getDeadline)
            .thenComparingInt(Task::getId))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByDeadline.size())
            .forEach(i -> {
                Task task = sortedByDeadline.get(i);
                task.setPriority(i+1);
            });
    }

}
