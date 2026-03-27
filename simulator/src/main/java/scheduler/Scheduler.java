package scheduler;

import java.math.BigDecimal;
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
import sampler.ConstantSampler;
import taskSet.Job;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.SampleDuration;
import utils.log.TraceLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;

    private TreeSet<Job> readyJobs;
    private final List<Job> blockedJobs = new LinkedList<>();
    private final Map<Task, Job> activeJobs = new HashMap<>();
    private Job lastJobExecuted;
    private final Duration simulationDuration;

    private final MyClock clock;
    private final TraceLogger logger;

    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();

    // Constructor
    /**
     * @param taskSet The taskset that will be schedule.
     * @param simulationDuration Must be expressed in milliseconds.
     */
    public Scheduler(TaskSet taskSet, double simulationDuration, TraceLogger logger) {
        this.taskSet = taskSet;
        this.simulationDuration = SampleDuration.sample(new ConstantSampler(new BigDecimal(simulationDuration)));
        this.clock = new MyClock();
        this.logger = logger;
        this.assignPriority();
    }

    // Getter and setter
    public TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected void setReadyJobs(TreeSet<Job> readyJobs) {
        this.readyJobs = readyJobs;
    }

    // Methods
    /**
     * Entry point for the analysis of the scheduler.
     */
    public final void analyze() throws DeadlineMissedException {
        this.resetState();
        this.scheduleFirstReleases();
        // Iterate over all events.
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            Duration nextEventTime = event.getTime();
            // Check if we exceed the simulation duration.
            if (nextEventTime.compareTo(this.simulationDuration) > 0)
                break;
            // Distribute available time over ready jobs.
            Duration availableTime = nextEventTime.minus(this.clock.getCurrentTime());
            this.distributeAvailableTime(availableTime);
            this.clock.advanceTo(nextEventTime);

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
        if (this.clock.getCurrentTime().compareTo(this.simulationDuration) < 0) {
            this.distributeAvailableTime(this.simulationDuration.minus(this.clock.getCurrentTime()));
            this.clock.advanceTo(this.simulationDuration);
            this.checkDeadlines();
        }
        this.logger.log("<" + this.clock.printCurrentTime() + ", end>\n");
    }

    private void resetState() {
        this.clock.advanceTo(Duration.ZERO);
        this.activeJobs.clear();
        this.blockedJobs.clear();
        this.lastJobExecuted = null;
        this.readyJobs = new TreeSet<>(Comparator.comparingInt(Job::getPriority));
        this.eventQueue.clear();
        for (Task task : this.taskSet.getTasks()) {
            task.resetJobCounter();
        }
    }

    // Helper
    private void scheduleFirstReleases() {
        for (Task task : this.taskSet.getTasks())
            eventQueue.add(new ReleaseEvent(Duration.ZERO, task));
    }

    private void releaseJob(Task task) {
        // First release new job
        Job newJob = task.releaseJob(this.clock.getCurrentTime());
        activeJobs.put(task, newJob);
        this.readyJobs.add(newJob);
        this.logger.log("<" + this.clock.printCurrentTime() + ", release " + newJob.toString() + ">");

        // Schedule deadline event for this job
        eventQueue.add(new DeadlineEvent(newJob.getAbsoluteDeadline(), newJob));

        // Than schedule new releases
        Duration nextPeriod = task.sampleNextPeriod();
        Duration nextReleaseTime = this.clock.getCurrentTime().plus(nextPeriod);
        if (nextReleaseTime.compareTo(this.simulationDuration) <= 0)
            eventQueue.add(new ReleaseEvent(nextReleaseTime, task));
    }

    private void distributeAvailableTime(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyJobs.isEmpty()) {
            Job highPriorityJob = this.readyJobs.pollFirst();
            // Preemption handling.
            if (this.lastJobIsPreempted(highPriorityJob))
                this.logger.log("<" + this.clock.printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
            // Execution
            this.logger.log("<" + this.clock.printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration timeToExecute = availableTime.compareTo(highPriorityJob.getRemainingExecutionTime()) < 0
                ? availableTime : highPriorityJob.getRemainingExecutionTime();
            Duration executedTime = highPriorityJob.execute(timeToExecute);
            this.clock.advanceBy(executedTime);
            
            if (highPriorityJob.isCompleted()) {
                highPriorityJob.setCompletionTime(this.clock.getCurrentTime());
                this.logger.log("<" + this.clock.printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
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
            if (job.isDeadlineMissed(this.clock.getCurrentTime())) {
                this.logger.log("<" + this.clock.printCurrentTime() + ", deadlineMiss " + job.toString() + ">\n");
                throw new DeadlineMissedException("Il task " + job.toString() + " ha superato la deadline");
            }
        }
    }

    private boolean lastJobIsPreempted(Job currentJob) {
        return this.lastJobExecuted!=null
                && !this.lastJobExecuted.equals(currentJob)
                && !this.lastJobExecuted.isCompleted();
    }

    // Hook methods
    protected abstract void assignPriority();

}
