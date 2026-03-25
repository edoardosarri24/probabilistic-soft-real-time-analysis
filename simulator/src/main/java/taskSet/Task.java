package taskSet;

import java.math.BigDecimal;
import java.time.Duration;

import org.oristool.simulator.samplers.Sampler;

import exeptions.DeadlineMissedException;
import scheduler.Scheduler;
import utils.MyClock;
import utils.Utils;
import utils.logger.MyLogger;
import utils.sampler.ConstantSampler;
import utils.sampler.SampleDuration;

public class Task {

    private final int id;
    private final Duration period;
    private final Duration deadline;
    private final Sampler executionTimeSampler;
    private Duration executionTime;
    private Duration remainingExecutionTime;
    private int nominalPriority;
    private int dinamicPriority;

    private static int idCounter = 1;
    private boolean isExecuted = false;

    // CONSTRUCTOR
    /**
     * Constructs a new Task with the specified period, deadline, and execution time sampler.
     *
     * @param period                the period of the task in milliseconds
     * @param deadline              the deadline of the task in milliseconds
     * @param executionTimeSampler the sampler for the execution time
     */
    public Task(double period, double deadline, Sampler executionTimeSampler) {
        this.id = idCounter++;
        this.period = SampleDuration.sample(new ConstantSampler(new BigDecimal(period)));
        this.deadline = SampleDuration.sample(new ConstantSampler(new BigDecimal(deadline)));
        this.executionTimeSampler = executionTimeSampler;
        this.executionTime = SampleDuration.sample(executionTimeSampler);
        this.remainingExecutionTime = this.executionTime;
    }

    // GETTER AND SETTER
    public Duration getPeriod() {
        return this.period;
    }

    public int getNominalPriority() {
        return this.nominalPriority;
    }

    public void setDinamicPriority(int dinamicPriority) {
        this.dinamicPriority = dinamicPriority;
    }

    public int getDinamicPriority() {
        return this.dinamicPriority;
    }

    public boolean getIsExecuted() {
        return this.isExecuted;
    }

    public int getId() {
        return this.id;
    }

    public void initPriority(int priority) {
        this.nominalPriority = priority;
        this.dinamicPriority = priority;
    }

    public Duration getDeadline() {
        return this.deadline;
    }

    public boolean toBeRelease() {
        return MyClock.getInstance().getCurrentTime().toMillis() % this.period.toMillis() == 0;
    }

    // METHOD
    public Duration execute(Duration availableTime, Scheduler scheduler) throws DeadlineMissedException {
        if (availableTime.compareTo(this.remainingExecutionTime) < 0) {
            this.remainingExecutionTime = this.remainingExecutionTime.minus(availableTime);
            MyLogger.log("<" + Utils.printCurrentTime() + ", execute " + this.toString() + ">");
            MyClock.getInstance().advanceBy(availableTime);
            return availableTime;
        } else {
            Duration executedTime = this.remainingExecutionTime;
            MyLogger.log("<" + Utils.printCurrentTime() + ", execute " + this.toString() + ">");
            MyClock.getInstance().advanceBy(executedTime);
            this.checkDeadlineMiss();
            this.isExecuted = true;
            MyLogger.log("<" + Utils.printCurrentTime() + ", complete " + this.toString() + ">");
            this.remainingExecutionTime = Duration.ZERO;
            return executedTime;
        }
    }

    void purelyPeriodicCheck() {
        if (this.period.compareTo(this.deadline) != 0)
            throw new IllegalArgumentException(
                "Il task " + this.id
                + " non è puramente periodico: ha periodo " + this.period
                + " e deadline " + this.deadline);
    }

    public void relasePeriodTask() throws DeadlineMissedException {
        if (!this.isExecuted)
            throw new DeadlineMissedException("Il task " + this.id + " ha superato la deadline");
        this.reset();
        MyLogger.log("<" + Utils.printCurrentTime() + ", release " + this.toString() + ">");
    }

    double utilizationFactor() {
        long period = this.period.toNanos();
        return (double) this.executionTime.toNanos() / period;
    }

    void periodAndDealineCheck() {
        if (this.period.compareTo(this.deadline) < 0)
            throw new IllegalArgumentException(
                "Il task " + this.id
                + " ha periodo " + this.period
                + " e deadline " + this.deadline
                + ". Il periodo non può essere minore della deadline");
    }

    public Duration nextDeadline() {
        Duration output = Duration.ZERO;
        while (MyClock.getInstance().getCurrentTime().minus(output).isPositive()) {
            if (output.plus(this.deadline).minus(MyClock.getInstance().getCurrentTime()).isPositive())
                break;
            output = output.plus(this.period);
        }
        output = output.plus(this.deadline);
        return output;
    }

    void reset() {
        this.executionTime = SampleDuration.sample(executionTimeSampler);
        this.remainingExecutionTime = this.executionTime;
        this.isExecuted = false;
    }

    // OBJECT METHODS
    @Override
    public String toString() {
        return "Task" + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    // HELPER
    private void checkDeadlineMiss() throws DeadlineMissedException {
        long numberOfPeriods = MyClock.getInstance().getCurrentTime().toNanos() / this.period.toNanos();
        if (MyClock.getInstance().getCurrentTime().toNanos() > this.period.toNanos()*numberOfPeriods+this.deadline.toNanos())
            throw new DeadlineMissedException("Il task " + this.id + " ha superato la deadline");
    }

}