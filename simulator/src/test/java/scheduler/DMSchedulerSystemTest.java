package scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exeptions.DeadlineMissedException;
import sampler.ConstantSampler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.NoLogger;

class DMSchedulerSystemTest {

    @Test
    @DisplayName("Single task should complete all jobs without missing deadlines")
    void singleTaskShouldCompleteSuccessfully() throws DeadlineMissedException {
        Task task = new Task(
            new ConstantSampler(new BigDecimal(10)),
            10.0,
            new ConstantSampler(new BigDecimal(4)));
        TaskSet taskSet = new TaskSet(task);
        Scheduler scheduler = new DMScheduler(
            taskSet,
            50.0,
            new NoLogger());
        scheduler.analyze();
        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        assertThat(executionTimes).containsKey(task);
        assertThat(executionTimes.get(task)).hasSize(6);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(50));
    }

    @Test
    @DisplayName("T1 (High Priority) should preempt T2 (Low Priority)")
    void dmShouldHandlePreemptionCorrectly() throws DeadlineMissedException {
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(10)),
            5.0,
            new ConstantSampler(new BigDecimal(2)));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(20)),
            15.0,
            new ConstantSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(task1, task2);
        Scheduler scheduler = new DMScheduler(
            taskSet,
            25.0,
            new NoLogger());
        scheduler.analyze();
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(25));
    }

    @Test
    @DisplayName("Overload should trigger DeadlineMissedException")
    void overloadShouldThrowException() {
        // Total load = 1.1
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(10)),
            10.0,
            new ConstantSampler(new BigDecimal(6)));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(10)),
            10.0,
            new ConstantSampler(new BigDecimal(5)));
        TaskSet taskSet = new TaskSet(task1, task2);
        Scheduler scheduler = new DMScheduler(
            taskSet,
            50.0,
            new NoLogger());
        assertThrows(DeadlineMissedException.class, scheduler::analyze);
    }
}
