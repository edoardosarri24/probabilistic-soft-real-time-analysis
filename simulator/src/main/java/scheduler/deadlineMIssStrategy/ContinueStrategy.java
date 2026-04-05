package scheduler.deadlineMIssStrategy;

import scheduler.FixedPriorityScheduler;
import taskSet.Job;

/**
 * Strategy that continues simulation even after a deadline miss.
 */
public class ContinueStrategy implements DeadlineMissStrategy {

    @Override
    public void handleDeadlineMiss(Job job, FixedPriorityScheduler scheduler) {
        scheduler.getLogger().log("<" + scheduler.getClock().printCurrentTime() + ", deadlineMiss " + job.toString() + " (continue)>");
        scheduler.getAbortedJobsCollector().addAbortedJobs(job.getTask());
    }

}
