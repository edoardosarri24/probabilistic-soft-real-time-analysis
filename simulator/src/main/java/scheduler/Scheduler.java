package scheduler;

import java.math.BigDecimal;
import java.time.Duration;

import exeptions.DeadlineMissedException;
import sampler.ConstantSampler;
import taskSet.TaskSet;
import utils.MyClock;
import utils.SampleDuration;
import utils.TaskExecutionTimeCollector;
import utils.log.MyLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final Duration simulationDuration;
    private final MyClock clock = new MyClock();
    private final MyLogger logger;
    private final TaskExecutionTimeCollector taskExecutionTimeCollector = new TaskExecutionTimeCollector();


    // Constructor
    public Scheduler(TaskSet taskSet, double simulationDuration, MyLogger logger) {
        this.taskSet = taskSet;
        this.simulationDuration = SampleDuration.sample(new ConstantSampler(new BigDecimal(simulationDuration)));
        this.logger = logger;
    }

    // Getter and setter
    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected MyClock getClock() {
        return this.clock;
    }

    protected MyLogger getLogger() {
        return this.logger;
    }

    protected Duration getSimulationDuration() {
        return this.simulationDuration;
    }

    protected TaskExecutionTimeCollector getTaskExecutionTimeCollector() {
        return this.taskExecutionTimeCollector;
    }


    // Methods
    /**
     * Entry point for the analysis of a scheduler.
     * @return The all task execution time sampled during the simulation.
     */
    public final TaskExecutionTimeCollector analyze() throws DeadlineMissedException {
        try {
            this.analyzeForSubClasses();
            return this.taskExecutionTimeCollector;
        } finally {
            this.logger.close();
        }
    }

    protected abstract void analyzeForSubClasses() throws DeadlineMissedException;

}
