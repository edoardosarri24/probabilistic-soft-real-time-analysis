package taskSet;

import java.time.Duration;
import java.util.Objects;

import utils.MyUtils;

/**
 * While a {@link Task} is the project, a job maintains the dynamic state of a single task's execution.
 */
public final class Job {

    private final int id;
    private final Task task;
    private final Duration absoluteDeadline;
    private Duration remainingExecutionTime;
    private Duration executionTime;
    private boolean isCompleted = false;
    private Duration completionTime = null;

    // Constructor
    public Job(Task task, int jobId, Duration releaseTime, Duration executionTime) {
        this.task = MyUtils.requireNonNull(task, "task");
        this.id = (int) MyUtils.requirePositive(jobId, "jobId");
        this.absoluteDeadline = MyUtils.requireNonNegative(releaseTime, "releaseTime").plus(task.getDeadline());
        this.executionTime = MyUtils.requireNonNegative(executionTime, "executionTime");
        this.remainingExecutionTime = executionTime;
    }

    public int getId() {
        return this.id;
    }

    public Task getTask() {
        return this.task;
    }

    public Duration getAbsoluteDeadline() {
        return this.absoluteDeadline;
    }

    public Duration getRemainingExecutionTime() {
        return this.remainingExecutionTime;
    }

    public int getPriority() {
        return this.task.getPriority();
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }
    public Duration getExecutionTime() {
        return this.executionTime;
    }

    /**
     * @param completionTime The absolute time when the job finished.
     */
    public void setCompletionTime(Duration completionTime) {
        this.completionTime = completionTime;
    }

    // Objects methods
    @Override
    public String toString() {
        return task.toString() + "/" + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        Job job = (Job) obj;
        return this.task.equals(job.task) && this.id == job.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, id);
    }

    // Methods
    /**
     * Executes the job for the specified duration. Reminder: Is the {@Scheduler} that is responsable
     * to set the completionTime if the job has finished.
     * @param durationAvailable The duration to execute.
     * @return The actual duration executed, that might be less than requested if job completes.
     */
    public Duration execute(Duration durationAvailable) {
        if (durationAvailable.compareTo(remainingExecutionTime) >= 0) {
            Duration durationExecuted = remainingExecutionTime;
            remainingExecutionTime = Duration.ZERO;
            isCompleted = true;
            return durationExecuted;
        } else {
            remainingExecutionTime = remainingExecutionTime.minus(durationAvailable);
            return durationAvailable;
        }
    }

    /**
     * Checks if the job has missed its deadline.
     * @param currentTime The current simulation time.
     * @return True if the deadline is missed.
     */
    public boolean isDeadlineMissed(Duration currentTime) {
        // Deadline miss if it not completed and currentTime reached deadline.
        if (!isCompleted && currentTime.compareTo(this.absoluteDeadline) >= 0)
            return true;
        // If job is completed, check if it finished after or before the deadline.
        if (isCompleted && Objects.nonNull(completionTime) && completionTime.compareTo(this.absoluteDeadline) > 0)
            return true;
        return false;
    }

}
