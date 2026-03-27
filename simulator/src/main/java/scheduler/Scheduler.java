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
    private final List<Job> blockedJobs = new LinkedList<>();
    private final Map<Task, Job> activeJobs = new HashMap<>();
    private Job lastJobExecuted;
    private final Duration simulationDuration;

    private final MyClock clock;
    private final TraceLogger logger;

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
    public void blockJob(Job job) {
        this.blockedJobs.add(job);
    }

    public void unblockJob(Job job) {
        this.blockedJobs.remove(job);
    }

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
        this.releaseFirstJobs();
        List<Duration> events = initEvents();
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(this.clock.getCurrentTime());
            this.distributeAvailableTime(availableTime);
            this.clock.advanceTo(nextEvent);
            this.checkDeadlines();
            this.releaseJobOfPeriodTasks();
        }
        this.logger.log("<" + this.clock.printCurrentTime() + ", end>\n");
    }

    private void resetState() {
        this.clock.advanceTo(Duration.ZERO);
        this.activeJobs.clear();
        this.blockedJobs.clear();
        this.lastJobExecuted = null;
        this.readyJobs = new TreeSet<>(Comparator.comparingInt(Job::getPriority));
        for (Task task : this.taskSet.getTasks()) {
            task.resetJobCounter();
        }
    }

    // Helper
    private void releaseFirstJobs() {
        this.readyJobs = new TreeSet<>(Comparator.comparingInt(Job::getPriority));
        for (Task task : this.taskSet.getTasks()) {
            Job firstJob = task.releaseJob(Duration.ZERO);
            activeJobs.put(task, firstJob);
            this.readyJobs.add(firstJob);
            this.logger.log("<" + this.clock.printCurrentTime() + ", release " + firstJob.toString() + ">");
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
                this.logger.log("<" + this.clock.printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
            // Execution
            this.logger.log("<" + this.clock.printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration timeToExecute = availableTime.compareTo(highPriorityJob.getRemainingExecutionTime()) < 0
                ? availableTime : highPriorityJob.getRemainingExecutionTime();
            Duration executedTime = highPriorityJob.execute(timeToExecute);
            this.clock.advanceBy(executedTime);
            this.checkDeadlines();
            if (highPriorityJob.isCompleted())
                this.logger.log("<" + this.clock.printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
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

    private void releaseJobOfPeriodTasks() throws DeadlineMissedException {
        Duration currentTime = this.clock.getCurrentTime();
        for (Task task : this.taskSet.getTasks()) {
            if (currentTime.toNanos() % task.getPeriod().toNanos() == 0) {
                Job newJob = task.releaseJob(currentTime);
                activeJobs.put(task, newJob);
                this.readyJobs.add(newJob);
                this.logger.log("<" + this.clock.printCurrentTime() + ", release " + newJob.toString() + ">");
            }
        }
    }

    // Hook methods
    protected abstract void assignPriority();

}
