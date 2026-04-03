package utils.collector;

import java.util.HashMap;
import java.util.Map;
import taskSet.Task;

public final class AbortedJobsCollector {

    private final Map<Task, Integer> abortedJobs = new HashMap<>();
    private final Map<Task, Integer> jobPerTaskReleased = new HashMap<>();

    // Methods
    public void addAbortedJobs(Task task) {
        this.abortedJobs.merge(task, 1, Integer::sum);
    }

    public void incrementJobPerTaskReleased(Task task) {
        this.jobPerTaskReleased.merge(task, 1, Integer::sum);
    }

    public int getAbortedJobsCount(Task task) {
        return this.abortedJobs.getOrDefault(task, 0);
    }

    public int getJobPerTaskReleased(Task task) {
        return this.jobPerTaskReleased.getOrDefault(task, 0);
    }

    public void clear() {
        this.abortedJobs.clear();
        this.jobPerTaskReleased.clear();
    }

}
