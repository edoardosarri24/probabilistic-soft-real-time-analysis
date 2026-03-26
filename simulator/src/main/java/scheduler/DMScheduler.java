package scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import taskSet.Task;
import taskSet.TaskSet;

public final class DMScheduler extends Scheduler {

    // Constructor
    public DMScheduler(TaskSet taskSet, double simulationDuration) {
        super(taskSet, simulationDuration);
        this.getTaskSet().purelyPeriodicCheck();
    }

    // methods
    @Override
    public boolean checkFeasibility() {
        return this.getTaskSet().hyperbolicBoundTest();
    }

    @Override
    protected void assignPriority() {
        List<Task> sortedByDeadline = getTaskSet().getTasks().stream()
            .sorted(Comparator.comparing(Task::getDeadline))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByDeadline.size())
            .forEach(i -> {
                Task task = sortedByDeadline.get(i);
                task.setPriority(i+1);
            }
        );
    }

}