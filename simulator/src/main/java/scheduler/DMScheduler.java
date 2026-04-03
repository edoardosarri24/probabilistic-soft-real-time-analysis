package scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.MyTraceLogger;
import utils.log.NoTraceLogger;

public final class DMScheduler extends FixedPriorityScheduler {

    // Constructor
    public DMScheduler(TaskSet taskSet, double simulationDuration, MyTraceLogger logger) {
        super(taskSet, simulationDuration, logger);
    }

    public DMScheduler(TaskSet taskSet, double simulationDuration) {
        super(taskSet, simulationDuration, new NoTraceLogger());
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
            }
        );
    }

}
