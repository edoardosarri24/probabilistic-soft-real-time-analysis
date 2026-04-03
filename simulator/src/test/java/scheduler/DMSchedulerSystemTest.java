package scheduler;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sampler.DeterministicSampler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.NoTraceLogger;

class DMSchedulerSystemTest {

    @Test
    @DisplayName("Scenario 1: Single task should complete all jobs without missing deadlines")
    void singleTaskShouldCompleteSuccessfully() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)));
        TaskSet taskSet = new TaskSet(t1);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 50.0, new NoTraceLogger());
        scheduler.analyze();

        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        assertThat(executionTimes.get(t1)).hasSize(5);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(50));
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(0);
    }

    @Test
    @DisplayName("Scenario 2: T1 (High Priority) should preempt T2 (Low Priority)")
    void dmShouldHandlePreemptionCorrectly() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 5.0, new DeterministicSampler(new BigDecimal(2)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(20)), 15.0, new DeterministicSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(t1, t2);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 25.0, new NoTraceLogger());
        scheduler.analyze();
        
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(25));
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(0);
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t2)).isEqualTo(0);
    }

    @Test
    @DisplayName("Scenario 3: Overload should result in aborted jobs instead of stopping the simulation")
    void overloadShouldResultInAbortedJobs() {
        // T1: P=10, D=10, C=8
        // T2: P=10, D=10, C=8
        // Total load = 1.6 (Overload)
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(8)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(8)));
        TaskSet taskSet = new TaskSet(t1, t2);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoTraceLogger());
        scheduler.analyze();
        
        // Simulation should complete up to 30ms
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(30));
        
        // At least T2 (lower priority) should have aborted jobs
        int aborted = scheduler.getAbortedJobsCollector().getAbortedJobsCount(t2);
        int completed = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().getOrDefault(t2, java.util.Collections.emptyList()).size();
        
        assertThat(aborted).isGreaterThan(0);
        
        // With P=10, D=10 and duration 30:
        // Job 1: Release 0, Deadline 10 -> Processed
        // Job 2: Release 10, Deadline 20 -> Processed
        // Job 3: Release 20, Deadline 30 -> Processed
        // Total processed: 3
        assertThat(aborted + completed).isEqualTo(6);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Idempotency: Running analyze() twice on the same scheduler should yield identical results")
    void analyzeShouldBeIdempotent() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)));
        TaskSet taskSet = new TaskSet(t1);
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoTraceLogger());

        // First run
        scheduler.analyze();
        int jobsFirstRun = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1).size();
        Duration clockFirstRun = scheduler.getClock().getCurrentTime();

        // Second run
        scheduler.analyze();
        int jobsSecondRun = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1).size();
        Duration clockSecondRun = scheduler.getClock().getCurrentTime();

        assertThat(jobsFirstRun).isEqualTo(jobsSecondRun).isEqualTo(3); // t=0, 10, 20
        assertThat(clockFirstRun).isEqualTo(clockSecondRun).isEqualTo(Duration.ofMillis(30));
    }

    @Test
    @DisplayName("Boundary: Job completing exactly at deadline should be a success")
    void jobCompletingAtDeadlineShouldSuccess() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(20)), 10.0, new DeterministicSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(t1);
        DMScheduler scheduler = new DMScheduler(taskSet, 20.0, new NoTraceLogger());

        assertThatNoException().isThrownBy(scheduler::analyze);
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(0);
    }

    @Test
    @DisplayName("Boundary: Task with zero execution time should complete instantly")
    void zeroExecutionTimeTaskShouldWork() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(0)));
        TaskSet taskSet = new TaskSet(t1);
        DMScheduler scheduler = new DMScheduler(taskSet, 20.0, new NoTraceLogger());

        scheduler.analyze();
        assertThat(scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime().get(t1)).hasSize(2);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(20));
        assertThat(scheduler.getAbortedJobsCollector().getAbortedJobsCount(t1)).isEqualTo(0);
    }

    @Test
    @DisplayName("Semantic: Nested preemption (T1 preempts T2, which already preempted T3)")
    void nestedPreemptionShouldBeHandledCorrectly() {
        java.util.List<String> logs = new java.util.ArrayList<>();
        utils.log.MyTraceLogger customLogger = new utils.log.MyTraceLogger() {
            @Override public void log(String msg) { logs.add(msg); }
            @Override public void close() {}
        };

        // T1: P=15, D=15, C=2
        // T2: P=30, D=30, C=20
        // T3: P=100, D=100, C=50
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(15)), 15.0, new DeterministicSampler(new BigDecimal(2)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(30)), 30.0, new DeterministicSampler(new BigDecimal(20)));
        Task t3 = new Task(new DeterministicSampler(new BigDecimal(100)), 100.0, new DeterministicSampler(new BigDecimal(50)));
        
        TaskSet taskSet = new TaskSet(t1, t2, t3);
        DMScheduler scheduler = new DMScheduler(taskSet, 60.0, customLogger);
        
        scheduler.analyze();

        String t1Name = t1.toString();
        String t2Name = t2.toString();

        // Check for preemption of T2 by T1 at t=15
        assertThat(logs).anyMatch(l -> l.contains("<15.000, preempt " + t2Name + "/1>"));
        
        // Check for nested preemption at t=45
        assertThat(logs).anyMatch(l -> l.contains("<45.000, preempt " + t2Name + "/2>"));
        assertThat(logs).anyMatch(l -> l.contains("<45.000, execute " + t1Name + "/4>"));
    }

    @Test
    @DisplayName("Offset: Task with firstReleaseTime > 0 should not start before its offset")
    void taskShouldRespectFirstReleaseTime() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)), new DeterministicSampler(new BigDecimal(15)));
        TaskSet taskSet = new TaskSet(t1);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoTraceLogger());
        scheduler.analyze();

        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        assertThat(executionTimes.get(t1)).hasSize(2);
        assertThat(scheduler.getClock().getCurrentTime()).isEqualTo(Duration.ofMillis(30));
    }

    @Test
    @DisplayName("Offset: Tasks with different firstReleaseTime schedule correctly")
    void tasksWithDifferentFirstReleaseTimesShouldScheduleCorrectly() {
        Task t1 = new Task(new DeterministicSampler(new BigDecimal(20)), 20.0, new DeterministicSampler(new BigDecimal(5)), new DeterministicSampler(new BigDecimal(0)));
        Task t2 = new Task(new DeterministicSampler(new BigDecimal(10)), 10.0, new DeterministicSampler(new BigDecimal(4)), new DeterministicSampler(new BigDecimal(7)));
        TaskSet taskSet = new TaskSet(t1, t2);
        
        DMScheduler scheduler = new DMScheduler(taskSet, 30.0, new NoTraceLogger());
        scheduler.analyze();

        Map<Task, java.util.List<Duration>> executionTimes = scheduler.getTaskExecutionTimeCollector().getTaskExecutionTime();
        assertThat(executionTimes.get(t1)).hasSize(2);
        assertThat(executionTimes.get(t2)).hasSize(3);
    }
}
