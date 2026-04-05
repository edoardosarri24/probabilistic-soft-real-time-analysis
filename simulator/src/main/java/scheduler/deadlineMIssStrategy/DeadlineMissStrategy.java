package scheduler.deadlineMIssStrategy;

import scheduler.FixedPriorityScheduler;
import taskSet.Job;

/**
 * Interface for deadline miss handling strategies.
 */
public interface DeadlineMissStrategy {

    /**
     * Handles a deadline miss for a specific job.
     * @param job The job that missed its deadline.
     * @param scheduler The scheduler where the deadline miss occurred.
     */
    void handleDeadlineMiss(Job job, FixedPriorityScheduler scheduler);

}
