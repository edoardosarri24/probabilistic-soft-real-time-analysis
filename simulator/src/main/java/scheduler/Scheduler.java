package scheduler;

import java.math.BigDecimal;
import java.time.Duration;

import sampler.DeterministicSampler;
import scheduler.deadlineMIssStrategy.DeadlineMissStrategy;
import taskSet.TaskSet;
import utils.MyClock;
import utils.MyUtils;
import utils.SampleDuration;
import utils.TraceLogger;
import utils.collector.AbortedJobsCollector;
import utils.collector.TaskExecutionTimeCollector;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final Duration simulationDuration;
    private final MyClock clock = new MyClock();
    private final TraceLogger logger = new TraceLogger();
    private final DeadlineMissStrategy strategy;
    private final TaskExecutionTimeCollector taskExecutionTimeCollector = new TaskExecutionTimeCollector();
    private final AbortedJobsCollector abortedJobsCollector = new AbortedJobsCollector();


    // Constructor
    public Scheduler(TaskSet taskSet, double simulationDuration, DeadlineMissStrategy strategy) {
        this.taskSet = MyUtils.requireNonNull(taskSet, "taskSet");
        this.simulationDuration = SampleDuration.sample(
            new DeterministicSampler(new BigDecimal(MyUtils.requireNonNegative(simulationDuration, "simulationDuration"))));
        this.strategy = MyUtils.requireNonNull(strategy, "strategy");
    }

    // Getter and setter
    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    public MyClock getClock() {
        return this.clock;
    }

    public TraceLogger getLogger() {
        return this.logger;
    }

    protected Duration getSimulationDuration() {
        return this.simulationDuration;
    }

    protected DeadlineMissStrategy getStrategy() {
        return this.strategy;
    }

    protected TaskExecutionTimeCollector getTaskExecutionTimeCollector() {
        return this.taskExecutionTimeCollector;
    }

    public AbortedJobsCollector getAbortedJobsCollector() {
        return this.abortedJobsCollector;
    }


    // Methods
    /**
     * Entry point for the analysis of a scheduler.
     * @return The all task execution time sampled during the simulation.
     */
    public final TaskExecutionTimeCollector analyze() {
        try {
            this.analyzeForSubClasses();
            return this.taskExecutionTimeCollector;
        } finally {
            this.logger.close();
        }
    }

    protected abstract void analyzeForSubClasses();

}
