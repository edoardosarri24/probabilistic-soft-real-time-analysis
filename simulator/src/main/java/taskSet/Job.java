package taskSet;

import java.time.Duration;

/**
 * Represents a single execution instance (a job) of a {@link Task}.
 * While a Task is the project, a job maintains the dynamic state of the task's execution.
 */
public final class Job {
    private final Task task;
    private final int id;
    private final Duration releaseTime;
    private final Duration absoluteDeadline;
    private Duration remainingExecutionTime;
    private boolean isCompleted = false;

    public Job(Task task, int jobId, Duration releaseTime, Duration executionTime) {
        this.task = task;
        this.id = jobId;
        this.releaseTime = releaseTime;
        this.absoluteDeadline = releaseTime.plus(task.getDeadline());
        this.remainingExecutionTime = executionTime;
    }

    public Task getTask() {
        return this.task;
    }

    public int getId() {
        return this.id;
    }

    public Duration getReleaseTime() {
        return this.releaseTime;
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

    // Methods
    /**
     * Executes the job for the specified duration.
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
        // A deadline is missed if the current time reaches or exceeds the absolute deadline and the job has not completed.
        return !isCompleted && currentTime.compareTo(absoluteDeadline) >= 0;
    }

    // Objects methods
    @Override
    public String toString() {
        return task.toString() + "/" + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Job job = (Job) obj;
        return task.equals(job.task) && releaseTime.equals(job.releaseTime);
    }

    @Override
    public int hashCode() {
        return task.hashCode()
            + releaseTime.hashCode()
            + absoluteDeadline.hashCode();
    }
}
