package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.TreeSet;

import event.DeadlineEvent;
import event.Event;
import event.ReleaseEvent;
import exeptions.DeadlineMissedException;
import taskSet.Job;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.MyLogger;

public abstract class FixedPriorityScheduler extends Scheduler  {

    private final TreeSet<Job> readyJobs = new TreeSet<>(
        Comparator.comparingInt(Job::getPriority)
            .thenComparingInt(job -> job.getTask().getId())
            .thenComparingInt(Job::getId)
    );
    private final List<Job> activeJobs = new LinkedList<>();
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    private Job lastJobExecuted;

    // Constructor
    /**
     * @param taskSet The taskset that will be schedule.
     * @param simulationDuration Must be expressed in milliseconds.
     */
    public FixedPriorityScheduler(TaskSet taskSet, double simulationDuration, MyLogger logger) {
        super(taskSet, simulationDuration, logger);
    }

    // Methods
    @Override
    protected final void analyzeForSubClasses() throws DeadlineMissedException {
        this.assignPriority();
        this.resetState();
        this.scheduleFirstReleases();
        // Iterate over all events.
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            Duration nextEventTime = event.getTime();
            // Check if we exceed the simulation duration.
            if (nextEventTime.compareTo(this.getSimulationDuration()) > 0)
                break;
            // Distribute available time over ready jobs.
            Duration availableTime = nextEventTime.minus(this.getClock().getCurrentTime());
            this.distributeAvailableTime(availableTime);
            // Advance with the clock time and check deadlines for all active jobs at this time point
            this.getClock().advanceTo(nextEventTime);
            this.checkDeadlines();
            // Collect and remove all events that occur at the same time. Then process the releases.
            List<Event> currentEvents = new LinkedList<>();
            currentEvents.add(event);
            while (!eventQueue.isEmpty() && eventQueue.peek().getTime().equals(nextEventTime))
                currentEvents.add(eventQueue.poll());
            currentEvents.stream()
                .filter(ReleaseEvent.class::isInstance)
                .map(ReleaseEvent.class::cast)
                .forEach(releasedEvent -> this.releaseJob(releasedEvent.getTask()));
        }
        // Maybe the current time hasn't already reached the simulation duration.
        if (this.getClock().getCurrentTime().compareTo(this.getSimulationDuration()) < 0) {
            Duration availableTime = this.getSimulationDuration().minus(this.getClock().getCurrentTime());
            this.distributeAvailableTime(availableTime);
            this.getClock().advanceTo(this.getSimulationDuration());
            this.checkDeadlines();
        }
        this.getLogger().log("<" + this.getClock().printCurrentTime() + ", end>\n");
    }

    // Helper

    private void resetState() {
        this.getClock().reset();
        this.activeJobs.clear();
        this.lastJobExecuted = null;
        this.readyJobs.clear();
        this.eventQueue.clear();
        this.getTaskExecutionTimeCollector().clear();
        for (Task task : this.getTaskSet().getTasks()) {
            task.resetJobCounter();
        }
    }

    private void scheduleFirstReleases() {
        for (Task task : this.getTaskSet().getTasks())
            eventQueue.add(new ReleaseEvent(task.getFirstReleaseTime(), task));
    }

    private void distributeAvailableTime(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyJobs.isEmpty()) {
            Job highPriorityJob = this.readyJobs.pollFirst();
            // Preemption handling.
            this.checkPreemption(highPriorityJob);
            // Execution
            this.getLogger().log("<" + this.getClock().printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration executedTime = highPriorityJob.execute(availableTime);
            if (highPriorityJob.isCompleted()) {
                highPriorityJob.setCompletionTime(this.getClock().getCurrentTime());
                this.activeJobs.remove(highPriorityJob);
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
            }
            // Advance clock, check deadlines and define the new available time.
            this.getClock().advanceBy(executedTime);
            this.checkDeadlines();
            availableTime = availableTime.minus(executedTime);
            // If the job has executed set it as the last executed job.
            if (executedTime.isPositive())
                this.lastJobExecuted = highPriorityJob;
            // If the job hasn't finished its execution we will consider it again.
            if (!highPriorityJob.isCompleted())
                this.readyJobs.add(highPriorityJob);
        }
    }

    private void checkPreemption(Job currentJob) {
        if(Objects.nonNull(this.lastJobExecuted)
                && !this.lastJobExecuted.equals(currentJob)
                && !this.lastJobExecuted.isCompleted())
            this.getLogger().log("<" + this.getClock().printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
    }

    /**
     * Check the deadlines miss for all active jobs.
     */
    private void checkDeadlines() throws DeadlineMissedException {
        for (Job job : activeJobs)
            if (job.isDeadlineMissed(this.getClock().getCurrentTime())) {
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", deadlineMiss " + job.toString() + ">\n");
                throw new DeadlineMissedException("Il task " + job.toString() + " ha superato la deadline");
            }
    }

    private void releaseJob(Task task) {
        // First release new job and trace its execution time.
        Job newJob = task.releaseJob(this.getClock().getCurrentTime());
        this.getTaskExecutionTimeCollector().add(task, newJob.getExecutionTime());
        activeJobs.add(newJob);
        this.readyJobs.add(newJob);
        this.getLogger().log("<" + this.getClock().printCurrentTime() + ", release " + newJob.toString() + ">");
        // Schedule deadline event for this job
        eventQueue.add(new DeadlineEvent(newJob.getAbsoluteDeadline(), newJob));
        // Then schedule next release
        Duration nextPeriod = task.sampleNextPeriod();
        Duration nextReleaseTime = this.getClock().getCurrentTime().plus(nextPeriod);
        eventQueue.add(new ReleaseEvent(nextReleaseTime, task));
    }

    // Hook methods for subclasses.
    protected abstract void assignPriority();

}
