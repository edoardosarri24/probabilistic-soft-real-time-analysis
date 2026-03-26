package taskSet;

import java.math.BigDecimal;
import java.time.Duration;

import org.oristool.simulator.samplers.Sampler;

import sampler.ConstantSampler;
import utils.SampleDuration;

public class Task {

    private final int id;
    private final Duration period;
    private final Duration deadline;
    private final Sampler executionTimeSampler;
    private int priority;
    private int jobCounter = 0;

    private static int idCounter = 1;

    // Constructor
    public Task(double period, double deadline, Sampler executionTimeSampler) {
        this.id = idCounter++;
        this.period = SampleDuration.sample(new ConstantSampler(new BigDecimal(period)));
        this.deadline = SampleDuration.sample(new ConstantSampler(new BigDecimal(deadline)));
        this.executionTimeSampler = executionTimeSampler;
    }

    // Getter and setter
    public Duration getPeriod() {
        return this.period;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getId() {
        return this.id;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Duration getDeadline() {
        return this.deadline;
    }

    public void resetJobCounter() {
        this.jobCounter = 0;
    }

    // Methods
    /**
     * Creates a new Job for this task at the given release time.
     * @param releaseTime the time at which the job is released
     * @return a new Job instance
     */
    public Job releaseJob(Duration releaseTime) {
        Duration executionTime = SampleDuration.sample(executionTimeSampler);
        return new Job(this, ++jobCounter, releaseTime, executionTime);
    }

    void purelyPeriodicCheck() {
        if (this.period.compareTo(this.deadline) != 0)
            throw new IllegalArgumentException(
                "Il task " + this.id
                + " non è puramente periodico: ha periodo " + this.period
                + " e deadline " + this.deadline);
    }

    double utilizationFactor() {
        // Sample a baseline execution time for utilization analysis
        Duration sampledET = SampleDuration.sample(executionTimeSampler);
        long period = this.period.toNanos();
        return (double) sampledET.toNanos() / period;
    }

    void periodAndDealineCheck() {
        if (this.period.compareTo(this.deadline) < 0)
            throw new IllegalArgumentException(
                "Il task " + this.id
                + " ha periodo " + this.period
                + " e deadline " + this.deadline
                + ". Il periodo non può essere minore della deadline");
    }

    // Objects methods
    @Override
    public String toString() {
        return "Task " + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}
