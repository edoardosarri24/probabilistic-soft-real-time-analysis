package scheduler;

import java.math.BigDecimal;
import java.time.Duration;

import exeptions.DeadlineMissedException;
import sampler.ConstantSampler;
import taskSet.TaskSet;
import utils.MyClock;
import utils.SampleDuration;
import utils.log.TraceLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final Duration simulationDuration;
    private final MyClock clock = new MyClock();
    private final TraceLogger logger;

    // Constructor
    public Scheduler(TaskSet taskSet, double simulationDuration, TraceLogger logger) {
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

    protected TraceLogger getLogger() {
        return this.logger;
    }

    protected Duration getSimulationDuration() {
        return this.simulationDuration;
    }


    // Methods
    /**
     * Entry point for the analysis of a scheduler.
     */
    public abstract void analyze() throws DeadlineMissedException;

}
