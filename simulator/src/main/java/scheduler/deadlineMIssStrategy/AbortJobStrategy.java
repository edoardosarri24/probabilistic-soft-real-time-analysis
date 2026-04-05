package scheduler.deadlineMIssStrategy;

import scheduler.FixedPriorityScheduler;
import taskSet.Job;

/**
 * Strategy that aborts the job that missed its deadline.
 */
public final class AbortJobStrategy implements DeadlineMissStrategy {

    @Override
    public void handleDeadlineMiss(Job job, FixedPriorityScheduler scheduler) {
        scheduler.getLogger().log("<" + scheduler.getClock().printCurrentTime() + ", deadlineMiss " + job.toString() + " (aborted)>");
        scheduler.abortJob(job);
        scheduler.getAbortedJobsCollector().addAbortedJobs(job.getTask());
    }

}
