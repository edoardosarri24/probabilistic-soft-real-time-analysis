package scheduler;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exeptions.DeadlineMissedException;
import sampler.DeterministicSampler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.NoLogger;

class DMSchedulerSystemTest {

    @Test
    @DisplayName("Scenario 1: Single task should complete all jobs without missing deadlines")
    void singleTaskShouldCompleteSuccessfully() throws DeadlineMissedException {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)));
        TaskSet taskSet = new TaskSet(t1);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 50.0, new NoLogger());
        scheduler.analyze();

        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        assertThat(executionTimes.get(t1)).hasSize(6);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(50));
    }

    @Test
    @DisplayName("Scenario 2: T1 (High Priority) should preempt T2 (Low Priority)")
    void dmShouldHandlePreemptionCorrectly() throws DeadlineMissedException {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 5.0, new DeterministicSampler(new BigDecimal(2)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(20)), 15.0, new DeterministicSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(t1, t2);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 25.0, new NoLogger());
        scheduler.analyze();
        
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(25));
    }

    @Test
    @DisplayName("Scenario 3: Overload should trigger DeadlineMissedException")
    void overloadShouldThrowException() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(6)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(5)));
        TaskSet taskSet = new TaskSet(t1, t2);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 50.0, new NoLogger());
        assertThrows(DeadlineMissedException.class, scheduler::analyze);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Idempotency: Running analyze() twice on the same scheduler should yield identical results")
    void analyzeShouldBeIdempotent() throws DeadlineMissedException {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)));
        TaskSet taskSet = new TaskSet(t1);
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoLogger());

        // First run
        scheduler.analyze();
        int jobsFirstRun = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1).size();
        Duration clockFirstRun = scheduler.getClock().getCurrentTime();

        // Second run
        scheduler.analyze();
        int jobsSecondRun = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1).size();
        Duration clockSecondRun = scheduler.getClock().getCurrentTime();

        assertThat(jobsFirstRun).isEqualTo(jobsSecondRun).isEqualTo(4); // t=0, 10, 20, 30
        assertThat(clockFirstRun).isEqualTo(clockSecondRun).isEqualTo(Duration.ofMillis(30));
    }

    @Test
    @DisplayName("Boundary: Job completing exactly at deadline should be a success")
    void jobCompletingAtDeadlineShouldSuccess() throws DeadlineMissedException {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(20)), 10.0, new DeterministicSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(t1);
        DMScheduler scheduler = new DMScheduler(taskSet, 20.0, new NoLogger());

        assertThatNoException().isThrownBy(scheduler::analyze);
    }

    @Test
    @DisplayName("Boundary: Task with zero execution time should complete instantly")
    void zeroExecutionTimeTaskShouldWork() throws DeadlineMissedException {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(0)));
        TaskSet taskSet = new TaskSet(t1);
        DMScheduler scheduler = new DMScheduler(taskSet, 20.0, new NoLogger());

        scheduler.analyze();
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(3);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(20));
    }

    @Test
    @DisplayName("Stability: Long-scale simulation (1M jobs) should have zero temporal drift")
    void longScaleSimulationShouldHaveZeroDrift() throws DeadlineMissedException {
        // Period: 0.123456 ms = 123456 ns
        BigDecimal periodValue = new BigDecimal("0.123456");
        Task t1 = new Task(new DeterministicSampler(periodValue), 1.0, new DeterministicSampler(new BigDecimal("0.01")));
        TaskSet taskSet = new TaskSet(t1);
        
        // Simulate for 1,000,000 periods
        double totalDurationMs = 1000000 * periodValue.doubleValue();
        DMScheduler scheduler = new DMScheduler(taskSet, totalDurationMs, new NoLogger());
        
        scheduler.analyze();
        
        // Expected total time in nanoseconds
        long expectedNanos = 1000000L * 123456L;
        Duration expectedDuration = Duration.ofNanos(expectedNanos);
        
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(expectedDuration);
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(1000001); // t=0 to t=1000000
    }

    @Test
    @DisplayName("Semantic: Nested preemption (T1 preempts T2, which already preempted T3)")
    void nestedPreemptionShouldBeHandledCorrectly() throws DeadlineMissedException {
        java.util.List<String> logs = new java.util.ArrayList<>();
        utils.log.MyLogger customLogger = new utils.log.MyLogger() {
            @Override public void log(String msg) { logs.add(msg); }
            @Override public void close() {}
        };

        // T1: P=15, D=15, C=2
        // T2: P=30, D=30, C=20 (Increased from 10 to ensure it's still running at t=45)
        // T3: P=100, D=100, C=50
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(15)), 15.0, new DeterministicSampler(new BigDecimal(2)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(30)), 30.0, new DeterministicSampler(new BigDecimal(20)));
        Task t3 = new Task(new DeterministicSampler(new BigDecimal(100)), 100.0, new DeterministicSampler(new BigDecimal(50)));
        
        TaskSet taskSet = new TaskSet(t1, t2, t3);
        DMScheduler scheduler = new DMScheduler(taskSet, 60.0, customLogger);
        
        scheduler.analyze();

        // Verification:
        // t=0: T1/1(0-2), T2/1(2-22), T3/1(22-...)
        // t=15: T1/2 releases -> preempts T2/1 (15-17). T2/1 resumes at 17.
        // t=22: T2/1 completes. T3/1 starts.
        // t=30: T1/3, T2/2 release -> T1/3(30-32), T2/2(32-52).
        // t=45: T1/4 releases -> PREEMPTS T2/2 (which was running 32-52).
        
        String t1Name = t1.toString();
        String t2Name = t2.toString();

        // Check for preemption of T2 by T1 at t=15
        assertThat(logs).anyMatch(l -> l.contains("<15.000, preempt " + t2Name + "/1>"));
        
        // Check for nested preemption at t=45
        // T1/4 preempts T2/2 (which already had preempted T3/1 implicitly by being higher priority)
        assertThat(logs).anyMatch(l -> l.contains("<45.000, preempt " + t2Name + "/2>"));
        assertThat(logs).anyMatch(l -> l.contains("<45.000, execute " + t1Name + "/4>"));
        
        // Ensure T2/2 resumes after T1/4 completes at t=47
        int completeT1Index = -1;
        int resumeT2Index = -1;
        for (int i = 0; i < logs.size(); i++) {
            if (logs.get(i).contains("<47.000, complete " + t1Name + "/4>")) completeT1Index = i;
            if (logs.get(i).contains("<47.000, execute " + t2Name + "/2>")) resumeT2Index = i;
        }
        assertThat(completeT1Index).isLessThan(resumeT2Index);
        assertThat(resumeT2Index).isNotEqualTo(-1);
    }

    @Test
    @DisplayName("Offset: Task with firstReleaseTime > 0 should not start before its offset")
    void taskShouldRespectFirstReleaseTime() throws DeadlineMissedException {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)), new DeterministicSampler(new BigDecimal(15)));
        TaskSet taskSet = new TaskSet(t1);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoLogger());
        scheduler.analyze();

        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        // Releases at 15, 25. 
        assertThat(executionTimes.get(t1)).hasSize(2);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(30));
    }

    @Test
    @DisplayName("Offset: Tasks with different firstReleaseTime schedule correctly")
    void tasksWithDifferentFirstReleaseTimesShouldScheduleCorrectly() throws DeadlineMissedException {
        // T1: P=20, D=20, C=5, Offset=0
        // T2: P=10, D=10, C=4, Offset=7
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(20)), 20.0, new DeterministicSampler(new BigDecimal(5)), new DeterministicSampler(new BigDecimal(0)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)), new DeterministicSampler(new BigDecimal(7)));
        TaskSet taskSet = new TaskSet(t1, t2);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoLogger());
        scheduler.analyze();

        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        // T1 released at 0, 20
        assertThat(executionTimes.get(t1)).hasSize(2);
        // T2 released at 7, 17, 27
        assertThat(executionTimes.get(t2)).hasSize(3);
    }
}
