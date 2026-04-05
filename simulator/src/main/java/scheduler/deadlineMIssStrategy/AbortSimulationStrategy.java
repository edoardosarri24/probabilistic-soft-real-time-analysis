package scheduler.deadlineMIssStrategy;

import scheduler.FixedPriorityScheduler;
import taskSet.Job;

/**
 * Strategy that aborts the simulation upon a deadline miss.
 */
public final class AbortSimulationStrategy implements DeadlineMissStrategy {

    @Override
    public void handleDeadlineMiss(Job job, FixedPriorityScheduler scheduler) {
        String logMessage = "<" + scheduler.getClock().printCurrentTime() + ", deadlineMiss " + job.toString() + " (simulation aborted)>";
        scheduler.getLogger().log(logMessage);
        throw new RuntimeException(logMessage);
    }

}
