package scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sampler.DeterministicSampler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.MyTraceLogger;
import utils.log.NoTraceLogger;

class DMSchedulerAdvancedSystemTest {

    @Test
    @DisplayName("Scenario: Abort-Preemption Chain - verifies fix for lastJobExecuted after abort")
    void abortPreemptionChainShouldNotLogFalsePreempt() {
        List<String> logs = new ArrayList<>();
        MyTraceLogger customLogger = new MyTraceLogger() {
            @Override public void log(String msg) { logs.add(msg); }
            @Override public void close() {}
        };

        // T1: P=10, D=10, C=5 (High Priority)
        // T2: P=100, D=20, C=15 (Low Priority) - Released at 0
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(5)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(100)), 20.0, new DeterministicSampler(new BigDecimal(15)));
        
        TaskSet taskSet = new TaskSet(t1, t2);
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 30.0, customLogger);
        scheduler.analyze();

        String t1Name = t1.toString();
        String t2Name = t2.toString();

        // Timeline expected:
        // 0.000: T1/1 and T2/1 released. T1/1 executes.
        // 5.000: T1/1 completes. T2/1 executes.
        // 10.000: T1/2 released. T1/2 preempts T2/1.
        // 15.000: T1/2 completes. T2/1 resumes.
        // 20.000: T2/1 deadline missed -> ABORTED. Then T1/3 released -> EXECUTE.
        // We must NOT see "preempt T2/1" after the abort log.

        assertThat(logs).anyMatch(l -> l.contains("<10.000, preempt " + t2Name + "/1>"));
        assertThat(logs).anyMatch(l -> l.contains("<20.000, deadlineMiss " + t2Name + "/1 (aborted)>"));
        
        // Verification of the fix: No "preempt Task 2/1" at time 20.000
        assertThat(logs).noneMatch(l -> l.contains("<20.000, preempt " + t2Name + "/1>"));
        assertThat(logs).anyMatch(l -> l.contains("<20.000, execute " + t1Name + "/3>"));
    }

    @Test
    @DisplayName("Scenario: Back-to-Back Execution - Job finishes exactly at next release")
    void backToBackExecution() {
        // T1: P=10, D=10, C=10
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(t1);
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 30.0, new NoTraceLogger());
        scheduler.analyze();

        // All 3 jobs should complete (0-10, 10-20, 20-30)
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(0);
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(3);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(30));
    }

    @Test
    @DisplayName("Scenario: All Deadlines Missed - High overload")
    void allDeadlinesMissed() {
        // T1: P=10, D=10, C=20 (Impossible to complete)
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(20)));
        TaskSet taskSet = new TaskSet(t1);
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 40.0, new NoTraceLogger());
        scheduler.analyze();

        // Every job should be aborted at its deadline (at 10, 20, 30, 40).
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(4);
        
        // We collect ALL released jobs' execution times, even if they abort.
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(4);
    }

    @Test
    @DisplayName("Scenario: Idle Gap - Clock jumps correctly between active periods")
    void idleGap() {
        // T1: P=100, D=10, C=5, offset=50
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(100)), 10.0, new DeterministicSampler(new BigDecimal(5)), new DeterministicSampler(new BigDecimal(50)));
        TaskSet taskSet = new TaskSet(t1);
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 100.0, new NoTraceLogger());
        scheduler.analyze();

        // 0-50: Idle
        // 50-55: T1/1 executes
        // 55-100: Idle
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(100));
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(1);
    }

    @Test
    @DisplayName("Scenario: Complex Mix - Preemption, Abort and Success")
    void complexMixScenario() {
        // T1: P=20, D=5, C=2 (High priority, very short deadline)
        // T2: P=40, D=40, C=30 (Low priority, long execution)
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(20)), 5.0, new DeterministicSampler(new BigDecimal(2)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(40)), 40.0, new DeterministicSampler(new BigDecimal(30)));
        
        TaskSet taskSet = new TaskSet(t1, t2);
        DeadlineMonotonicScheduler scheduler = new DeadlineMonotonicScheduler(taskSet, 80.0, new NoTraceLogger());
        scheduler.analyze();

        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(0);
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t2)).isEqualTo(0);
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(4);
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t2)).hasSize(2);
    }
}
