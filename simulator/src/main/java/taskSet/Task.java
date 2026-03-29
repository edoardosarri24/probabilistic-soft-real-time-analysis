package taskSet;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

import org.oristool.simulator.samplers.Sampler;

import sampler.DeterministicSampler;
import utils.MyUtils;
import utils.SampleDuration;

public final class Task {

    private final int id;
    private final Sampler periodSampler;
    private final Duration deadline;
    private final Sampler executionTimeSampler;
    private final Duration firstReleaseTime;
    private int priority;
    private int jobCounter = 1;

    private static int idCounter = 1;

    // Constructor
    /**
     * @param periodSampler The distribution from which the period will be sampled.
     * @param deadline Must be express in milliseconds. It's the relative deadline.
     * @param executionTimeSampler The distribution from which the execution time will be sampled.
     */
    public Task(Sampler periodSampler, double deadline, Sampler executionTimeSampler) {
        this(periodSampler, deadline, executionTimeSampler, new DeterministicSampler(BigDecimal.ZERO));
    }

    /**
     * @param periodSampler The distribution from which the period will be sampled.
     * @param deadline Must be express in milliseconds. It's the relative deadline.
     * @param executionTimeSampler The distribution from which the execution time will be sampled.
     * @param firstReleaseTimeSample It's the distribution from which the first job release time will be sampled.
     */
    public Task(Sampler periodSampler, double deadline, Sampler executionTimeSampler, Sampler firstReleaseTimeSample) {
        this.id = idCounter++;
        this.periodSampler = MyUtils.requireNonNull(periodSampler, "periodSampler");
        this.deadline = SampleDuration.sample(
            new DeterministicSampler(new BigDecimal(MyUtils.requireNonNegative(deadline, "deadline"))));
        this.executionTimeSampler = MyUtils.requireNonNull(executionTimeSampler, "executionTimeSampler");
        this.firstReleaseTime = SampleDuration.sample(MyUtils.requireNonNull(firstReleaseTimeSample, "firstReleaseTimeSample"));
    }

    // Getter and setter
    public int getId() {
        return this.id;
    }

    public Duration sampleNextPeriod() {
        return SampleDuration.sample(this.periodSampler);
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Duration getDeadline() {
        return this.deadline;
    }

    public Duration getFirstReleaseTime() {
        return this.firstReleaseTime;
    }

    public void resetJobCounter() {
        this.jobCounter = 1;
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
        Task task = (Task) obj;
        return this.id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // Methods
    /**
     * Creates a new Job for this task.
     * @param releaseTime The time at which the job is released.
     * @return A new Job instance.
     */
    public Job releaseJob(Duration releaseTime) {
        Duration jobExecutionTime = SampleDuration.sample(executionTimeSampler);
        Job newJob = new Job(this, jobCounter++, releaseTime, jobExecutionTime);
        return newJob;
    }

}
