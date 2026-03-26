package scheduler;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import exeptions.DeadlineMissedException;
import sampler.ConstantSampler;
import taskSet.Job;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.SampleDuration;
import utils.Utils;
import utils.logger.TraceLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;

    private TreeSet<Job> readyJobs;
    private List<Job> blockedJobs = new LinkedList<>();
    private Map<Task, Job> activeJobs = new HashMap<>();
    private Job lastJobExecuted;
    private final Duration simulationDuration;

    protected final MyClock clock;
    protected final TraceLogger logger;

    // Constructor
    /**
     * @param taskSet The taskset that will be schedule.
     * @param simulationDuration Must be expressed in milliseconds.
     */
    public Scheduler(TaskSet taskSet, double simulationDuration, MyClock clock, TraceLogger logger) {
        this.taskSet = taskSet;
        this.simulationDuration = SampleDuration.sample(new ConstantSampler(new BigDecimal(simulationDuration)));
        this.clock = clock;
        this.logger = logger;
        this.assignPriority();
    }

    // Getter and setter
    public void blockJob(Job job) {
        this.blockedJobs.add(job);
    }

    public void unblockJob(Job job) {
        this.blockedJobs.remove(job);
    }

    public TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected void setReadyJobs(TreeSet<Job> readyTasks) {
        this.readyJobs = readyTasks;
    }

    // Methods
    /**
     * Entry point for the analysis of the scheduler.
     */
    public final void analyze() throws DeadlineMissedException {
        this.releaseFirstJobs();
        List<Duration> events = initEvents();
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(clock.getCurrentTime());
            this.distributeAvailableTime(availableTime);
            clock.advanceTo(nextEvent);
            this.releaseJobOfPeriodTasks();
        }
        logger.log("<" + clock.printCurrentTime() + ", end>\n");
    }

    // Helper
    private void releaseFirstJobs() {
        this.readyJobs = new TreeSet<>(Comparator.comparingInt(Job::getPriority));
        for (Task task : this.taskSet.getTasks()) {
            Job firstJob = task.releaseJob(Duration.ZERO);
            activeJobs.put(task, firstJob);
            this.readyJobs.add(firstJob);
            logger.log("<" + clock.printCurrentTime() + ", release " + firstJob.toString() + ">");
        }
    }

    private List<Duration> initEvents() {
        List<Duration> periods = new LinkedList<>();
        for (Task task : this.taskSet.getTasks())
            periods.add(task.getPeriod());
        List<Duration> events = Utils.generatePeriodUpToMax(periods, this.simulationDuration);
        return events;
    }

    private void distributeAvailableTime(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyJobs.isEmpty()) {
            Job highPriorityJob = this.readyJobs.pollFirst();
            // Preemption handling.
            if (this.lastJobIsPreempted(highPriorityJob))
                logger.log("<" + clock.printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
            // Execution
            logger.log("<" + clock.printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration timeToExecute = availableTime.compareTo(highPriorityJob.getRemainingExecutionTime()) < 0
                ? availableTime : highPriorityJob.getRemainingExecutionTime();
            Duration executedTime = highPriorityJob.execute(timeToExecute);
            clock.advanceBy(executedTime);
            if (highPriorityJob.isDeadlineMissed(clock.getCurrentTime())) {
                logger.log("<" + clock.printCurrentTime() + ", deadlineMiss " + highPriorityJob.toString() + ">\n");
                throw new DeadlineMissedException("Il task " + highPriorityJob.getTask().getId() + " ha superato la deadline");
            }
            if (highPriorityJob.isCompleted())
                logger.log("<" + clock.printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
            if (executedTime.isPositive())
                this.lastJobExecuted = highPriorityJob;
            availableTime = availableTime.minus(executedTime);
            if (!highPriorityJob.isCompleted() && !this.blockedJobs.contains(highPriorityJob))
                this.readyJobs.add(highPriorityJob);
        }
    }

    private boolean lastJobIsPreempted(Job currentJob) {
        return this.lastJobExecuted!=null
                && !this.lastJobExecuted.equals(currentJob)
                && !this.lastJobExecuted.isCompleted();
    }

    private void releaseJobOfPeriodTasks() throws DeadlineMissedException {
        Duration currentTime = clock.getCurrentTime();
        for (Task task : this.taskSet.getTasks()) {
            // Check if it's time to release a new job.
            if (currentTime.toNanos() % task.getPeriod().toNanos() == 0) {
                Job activeJob = activeJobs.get(task);
                // Deadline violation if job is not completed.
                if (activeJob != null && !activeJob.isCompleted()) {
                    logger.log("<" + clock.printCurrentTime() + ", deadlineMiss " + task.toString() + ">\n");
                    throw new DeadlineMissedException("Il task " + task.toString() + " ha superato la deadline");
                }
                // Otherwise release new job.
                Job newJob = task.releaseJob(currentTime);
                activeJobs.put(task, newJob);
                this.readyJobs.add(newJob);
                logger.log("<" + clock.printCurrentTime() + ", release " + newJob.toString() + ">");
            }
        }
    }

    // Hook methods
    protected abstract void assignPriority();

    public abstract boolean checkFeasibility();

}
