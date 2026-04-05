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
import scheduler.deadlineMIssStrategy.DeadlineMissStrategy;
import taskSet.Job;
import taskSet.Task;
import taskSet.TaskSet;

public abstract class FixedPriorityScheduler extends Scheduler  {

    private final TreeSet<Job> readyJobs = new TreeSet<>(
        Comparator.comparingInt(Job::getPriority)
            .thenComparingInt(job -> job.getTask().getId())
            .thenComparingInt(Job::getId));
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    private Job lastJobExecuted;

    // Constructor
    /**
     * @param taskSet The taskset that will be schedule.
     * @param simulationDuration Must be expressed in milliseconds.
     */
    public FixedPriorityScheduler(TaskSet taskSet, double simulationDuration, DeadlineMissStrategy strategy) {
        super(taskSet, simulationDuration, strategy);
    }

    // Methods
    @Override
    protected final void analyzeForSubClasses() {
        this.assignPriority();
        this.resetState();
        this.scheduleFirstReleases();
        // Iterate over all events.
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            Duration nextEventTime = event.getTime();
            // Distribute available time over ready jobs.
            Duration availableTime = nextEventTime.minus(this.getClock().getCurrentTime());
            this.distributeAvailableTime(availableTime);
            // Advance with the clock time.
            this.getClock().advanceTo(nextEventTime);
            // Collect and remove all events that occur at the same time.
            List<Event> currentEvents = new LinkedList<>();
            currentEvents.add(event);
            while (!eventQueue.isEmpty() && eventQueue.peek().getTime().equals(nextEventTime))
                currentEvents.add(eventQueue.poll());
            // Handle events just retrived.
            currentEvents.stream()
                .filter(DeadlineEvent.class::isInstance)
                .map(DeadlineEvent.class::cast)
                .forEach(deadlineEvent -> this.handleDeadlineMiss(deadlineEvent.getJob()));
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
        }
        this.getLogger().log("<" + this.getClock().printCurrentTime() + ", end>\n");
    }

    // Helper
    private void resetState() {
        this.getClock().reset();
        this.lastJobExecuted = null;
        this.readyJobs.clear();
        this.eventQueue.clear();
        this.getTaskExecutionTimeCollector().clear();
        this.getAbortedJobsCollector().clear();
        for (Task task : this.getTaskSet().getTasks())
            task.resetJobCounter();
    }

    private void scheduleFirstReleases() {
        for (Task task : this.getTaskSet().getTasks()) {
            if (task.getFirstReleaseTime().compareTo(this.getSimulationDuration()) < 0)
                eventQueue.add(new ReleaseEvent(task.getFirstReleaseTime(), task));
        }
    }

    private void distributeAvailableTime(Duration availableTime) {
        while (availableTime.isPositive() && !this.readyJobs.isEmpty()) {
            Job highPriorityJob = this.readyJobs.first();
            // Preemption handling.
            this.checkPreemption(highPriorityJob);
            // Execution
            if (!highPriorityJob.equals(this.lastJobExecuted))
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration executedTime = highPriorityJob.execute(availableTime);
            // Advance clock and define the new available time.
            this.getClock().advanceBy(executedTime);
            availableTime = availableTime.minus(executedTime);
            if (highPriorityJob.isCompleted()) {
                highPriorityJob.setCompletionTime(this.getClock().getCurrentTime());
                this.readyJobs.remove(highPriorityJob);
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
            }
            // If the job has executed set it as the last executed job.
            if (executedTime.isPositive())
                this.lastJobExecuted = highPriorityJob;
        }
    }

    private void handleDeadlineMiss(Job job) {
        if (job.isDeadlineMissed(this.getClock().getCurrentTime())) {
            this.getStrategy().handleDeadlineMiss(job, this);
        }
    }

    public void abortJob(Job job) {
        this.readyJobs.remove(job);
        if (job.equals(this.lastJobExecuted))
            this.lastJobExecuted = null;
    }

    private void checkPreemption(Job currentJob) {
        if(Objects.nonNull(this.lastJobExecuted)
                && !this.lastJobExecuted.equals(currentJob)
                && !this.lastJobExecuted.isCompleted())
            this.getLogger().log("<" + this.getClock().printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
    }

    private void releaseJob(Task task) {
        // First release new job.
        Job newJob = task.releaseJob(this.getClock().getCurrentTime());
        this.readyJobs.add(newJob);
        this.getLogger().log("<" + this.getClock().printCurrentTime() + ", release " + newJob.toString() + ">");
        // Track released job
        this.getAbortedJobsCollector().incrementJobPerTaskReleased(task);
        this.getTaskExecutionTimeCollector().add(newJob.getTask(), newJob.getExecutionTime());
        // Schedule deadline event for this job (only if within simulation duration)
        if (newJob.getAbsoluteDeadline().compareTo(this.getSimulationDuration()) <= 0)
            eventQueue.add(new DeadlineEvent(newJob.getAbsoluteDeadline(), newJob));
        // Then schedule next release (only if strictly before simulation duration)
        Duration nextPeriod = task.sampleNextPeriod();
        Duration nextReleaseTime = this.getClock().getCurrentTime().plus(nextPeriod);
        if (nextReleaseTime.compareTo(this.getSimulationDuration()) < 0)
            eventQueue.add(new ReleaseEvent(nextReleaseTime, task));
    }

    // Hook methods for subclasses.
    protected abstract void assignPriority();

}
