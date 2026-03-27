package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;

import event.DeadlineEvent;
import event.Event;
import event.ReleaseEvent;
import exeptions.DeadlineMissedException;
import taskSet.Job;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.TraceLogger;

public abstract class FixedPriorityScheduler extends Scheduler  {

    private TreeSet<Job> readyJobs;
    private final List<Job> blockedJobs = new LinkedList<>();
    private final Map<Task, Job> activeJobs = new HashMap<>();
    private Job lastJobExecuted;
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();

    // Constructor
    /**
     * @param taskSet The taskset that will be schedule.
     * @param simulationDuration Must be expressed in milliseconds.
     */
    public FixedPriorityScheduler(TaskSet taskSet, double simulationDuration, TraceLogger logger) {
        super(taskSet, simulationDuration, logger);
        this.assignPriority();
    }

    // Methods
    @Override
    public final void analyze() throws DeadlineMissedException {
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
            this.getClock().advanceTo(nextEventTime);

            // Collect all events at the same time to process them in batch
            List<Event> currentEvents = new LinkedList<>();
            currentEvents.add(event);
            while (!eventQueue.isEmpty() && eventQueue.peek().getTime().equals(nextEventTime))
                currentEvents.add(eventQueue.poll());

            // Check deadlines for all active jobs at this time point
            this.checkDeadlines();

            // Process releases
            for (Event e : currentEvents) {
                if (e instanceof ReleaseEvent) {
                    this.releaseJob(((ReleaseEvent) e).getTask());
                }
                // DeadlineEvents are just marker points for checkDeadlines(), 
                // no extra action needed here.
            }
        }
        // Execute the last ready jobs.
        if (this.getClock().getCurrentTime().compareTo(this.getSimulationDuration()) < 0) {
            this.distributeAvailableTime(this.getSimulationDuration().minus(this.getClock().getCurrentTime()));
            this.getClock().advanceTo(this.getSimulationDuration());
            this.checkDeadlines();
        }
        this.getLogger().log("<" + this.getClock().printCurrentTime() + ", end>\n");
    }

    // Helper
    private void scheduleFirstReleases() {
        for (Task task : this.getTaskSet().getTasks())
            eventQueue.add(new ReleaseEvent(Duration.ZERO, task));
    }

    private void releaseJob(Task task) {
        // First release new job
        Job newJob = task.releaseJob(this.getClock().getCurrentTime());
        activeJobs.put(task, newJob);
        this.readyJobs.add(newJob);
        this.getLogger().log("<" + this.getClock().printCurrentTime() + ", release " + newJob.toString() + ">");

        // Schedule deadline event for this job
        eventQueue.add(new DeadlineEvent(newJob.getAbsoluteDeadline(), newJob));

        // Than schedule new releases
        Duration nextPeriod = task.sampleNextPeriod();
        Duration nextReleaseTime = this.getClock().getCurrentTime().plus(nextPeriod);
        if (nextReleaseTime.compareTo(this.getSimulationDuration()) <= 0)
            eventQueue.add(new ReleaseEvent(nextReleaseTime, task));
    }

    private void distributeAvailableTime(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyJobs.isEmpty()) {
            Job highPriorityJob = this.readyJobs.pollFirst();
            // Preemption handling.
            if (this.lastJobIsPreempted(highPriorityJob))
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
            // Execution
            this.getLogger().log("<" + this.getClock().printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration timeToExecute = availableTime.compareTo(highPriorityJob.getRemainingExecutionTime()) < 0
                ? availableTime : highPriorityJob.getRemainingExecutionTime();
            Duration executedTime = highPriorityJob.execute(timeToExecute);
            this.getClock().advanceBy(executedTime);
            
            if (highPriorityJob.isCompleted()) {
                highPriorityJob.setCompletionTime(this.getClock().getCurrentTime());
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
            }

            this.checkDeadlines();
            
            if (executedTime.isPositive())
                this.lastJobExecuted = highPriorityJob;
            availableTime = availableTime.minus(executedTime);
            if (!highPriorityJob.isCompleted() && !this.blockedJobs.contains(highPriorityJob))
                this.readyJobs.add(highPriorityJob);
        }
    }

    private void checkDeadlines() throws DeadlineMissedException {
        for (Job job : activeJobs.values()) {
            if (job.isDeadlineMissed(this.getClock().getCurrentTime())) {
                this.getLogger().log("<" + this.getClock().printCurrentTime() + ", deadlineMiss " + job.toString() + ">\n");
                throw new DeadlineMissedException("Il task " + job.toString() + " ha superato la deadline");
            }
        }
    }

    private void resetState() {
        this.getClock().advanceTo(Duration.ZERO);
        this.activeJobs.clear();
        this.blockedJobs.clear();
        this.lastJobExecuted = null;
        this.readyJobs = new TreeSet<>(Comparator.comparingInt(Job::getPriority));
        this.eventQueue.clear();
        for (Task task : this.getTaskSet().getTasks()) {
            task.resetJobCounter();
        }
    }

    private boolean lastJobIsPreempted(Job currentJob) {
        return this.lastJobExecuted!=null
                && !this.lastJobExecuted.equals(currentJob)
                && !this.lastJobExecuted.isCompleted();
    }

    // Hook methods for subclasses.
    protected abstract void assignPriority();

}
