package scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sampler.DeterministicSampler;
import scheduler.deadlineMIssStrategy.AbortJobStrategy;
import scheduler.deadlineMIssStrategy.AbortSimulationStrategy;
import scheduler.deadlineMIssStrategy.ContinueStrategy;
import taskSet.Task;
import taskSet.TaskSet;

class DeadlineMissStrategyTest {

    @Test
    @DisplayName("AbortJobStrategy: Should abort the job and continue simulation")
    void testAbortJobStrategy() {
        // Task: Period=10, Deadline=5, Exec=8 (Misses deadline at t=5)
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 5.0, new DeterministicSampler(new BigDecimal(8)));
        TaskSet taskSet = new TaskSet(t1);
        
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 20.0, new AbortJobStrategy());
        scheduler.analyze();

        // Simulation should reach end time
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(20));
        // Jobs at t=0 and t=10 should both miss deadline at t=5 and t=15
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(2);
        // Execution times are recorded at release, so we expect 2 entries even if aborted
        List<Duration> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().getOrDefault(t1, List.of());
        assertThat(executionTimes).hasSize(2);
    }

    @Test
    @DisplayName("AbortSimulationStrategy: Should throw exception on first deadline miss")
    void testAbortSimulationStrategy() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 5.0, new DeterministicSampler(new BigDecimal(8)));
        TaskSet taskSet = new TaskSet(t1);
        
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 20.0, new AbortSimulationStrategy());
        
        assertThatThrownBy(scheduler::analyze)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("deadlineMiss");
            
        // Clock should be at the deadline of the first job (5ms)
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(5));
    }

    @Test
    @DisplayName("ContinueStrategy: Should complete the job even after deadline miss")
    void testContinueStrategy() {
        // Task: Period=20, Deadline=10, Exec=15
        // Job 1 released at 0, deadline 10. Completes at 15 (Miss!)
        // Job 2 released at 20, deadline 30. Completes at 35 (Miss!)
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(20)), 10.0, new DeterministicSampler(new BigDecimal(15)));
        TaskSet taskSet = new TaskSet(t1);
        
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 40.0, new ContinueStrategy());
        scheduler.analyze();

        // Simulation should reach end time
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(40));
        // Both jobs missed their deadlines
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(2);
        // But both jobs SHOULD have completed their execution
        List<Duration> completions = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1);
        assertThat(completions).hasSize(2);
    }
}
