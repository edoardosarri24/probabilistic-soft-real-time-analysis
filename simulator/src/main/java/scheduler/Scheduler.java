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
import utils.logger.MyLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;

    private TreeSet<Job> readyJobs;
    private List<Job> blockedJobs = new LinkedList<>();
    private Map<Task, Job> activeJobs = new HashMap<>();
    private Job lastJobExecuted;
    private final Duration simulationDuration;

    // Constructor
    public Scheduler(TaskSet taskSet, double simulationDuration) {
        this.taskSet = taskSet;
        this.simulationDuration = SampleDuration.sample(new ConstantSampler(new BigDecimal(simulationDuration)));
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
        this.reset();
        this.releaseFirstJobs();
        List<Duration> events = initEvents();
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(MyClock.getCurrentTime());
            this.distributeAvailableTime(availableTime);
            MyClock.advanceTo(nextEvent);
            this.releaseJobOfPeriodTasks();
        }
        MyLogger.log("<" + MyClock.printCurrentTime() + ", end>\n");
    }

    public final void scheduleDataset(int trace) {
        for (int i=0; i<trace; i++) {
            try {
                this.analyze();
            } catch (DeadlineMissedException e) {
                continue;
            }
        }
    }

    public void addReadyJob(Job job) {
        this.readyJobs.add(job);
    }

    // Helper
    private List<Duration> initEvents() {
        List<Duration> periods = new LinkedList<>();
        for (Task task : this.taskSet.getTasks())
            periods.add(task.getPeriod());
        List<Duration> events = Utils.generatePeriodUpToMax(periods, this.simulationDuration);
        return events;
    }

    private void releaseFirstJobs() {
        this.readyJobs = new TreeSet<>(Comparator.comparingInt(Job::getPriority));
        for (Task task : this.taskSet.getTasks()) {
            Job firstJob = task.releaseJob(Duration.ZERO);
            activeJobs.put(task, firstJob);
            this.addReadyJob(firstJob);
            MyLogger.log("<" + MyClock.printCurrentTime() + ", release " + firstJob.toString() + ">");
        }
    }

    private void distributeAvailableTime(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyJobs.isEmpty()) {
            Job highPriorityJob = this.readyJobs.pollFirst();
            // Preemption handling.
            if (this.lastJobIsPreempted(highPriorityJob))
                MyLogger.log("<" + MyClock.printCurrentTime() + ", preempt " + this.lastJobExecuted.toString() + ">");
            // Execution
            MyLogger.log("<" + MyClock.printCurrentTime() + ", execute " + highPriorityJob.toString() + ">");
            Duration timeToExecute = availableTime.compareTo(highPriorityJob.getRemainingExecutionTime()) < 0
                ? availableTime : highPriorityJob.getRemainingExecutionTime();
            Duration executedTime = highPriorityJob.execute(timeToExecute);
            MyClock.advanceBy(executedTime);
            if (highPriorityJob.isDeadlineMissed(MyClock.getCurrentTime())) {
                MyLogger.log("<" + MyClock.printCurrentTime() + ", deadlineMiss " + highPriorityJob.toString() + ">\n");
                throw new DeadlineMissedException("Il task " + highPriorityJob.getTask().getId() + " ha superato la deadline");
            }
            if (highPriorityJob.isCompleted())
                MyLogger.log("<" + MyClock.printCurrentTime() + ", complete " + highPriorityJob.toString() + ">");
            if (executedTime.isPositive())
                this.lastJobExecuted = highPriorityJob;
            availableTime = availableTime.minus(executedTime);
            if (!highPriorityJob.isCompleted() && !this.blockedJobs.contains(highPriorityJob))
                this.addReadyJob(highPriorityJob);
        }
    }

    private boolean lastJobIsPreempted(Job currentJob) {
        return this.lastJobExecuted!=null
                && !this.lastJobExecuted.equals(currentJob)
                && !this.lastJobExecuted.isCompleted();
    }

    private void releaseJobOfPeriodTasks() throws DeadlineMissedException {
        Duration currentTime = MyClock.getCurrentTime();
        for (Task task : this.taskSet.getTasks()) {
            // Check if it's time to release a new job.
            if (currentTime.toNanos() % task.getPeriod().toNanos() == 0) {
                Job activeJob = activeJobs.get(task);
                // Deadline violation if job is not completed.
                if (activeJob != null && !activeJob.isCompleted()) {
                    MyLogger.log("<" + MyClock.printCurrentTime() + ", deadlineMiss " + task.toString() + ">\n");
                    throw new DeadlineMissedException("Il task " + task.toString() + " ha superato la deadline");
                }
                // Otherwise release new job.
                Job newJob = task.releaseJob(currentTime);
                activeJobs.put(task, newJob);
                this.addReadyJob(newJob);
                MyLogger.log("<" + MyClock.printCurrentTime() + ", release " + newJob.toString() + ">");
            }
        }
    }

    private void reset() {
        MyClock.reset();
        this.activeJobs.clear();
        this.lastJobExecuted = null;
        this.assignPriority();
        for (Task task : this.taskSet.getTasks()) {
            task.resetJobCounter();
        }
    }

    // Hook methods
    protected abstract void assignPriority();

    public abstract boolean checkFeasibility();

}