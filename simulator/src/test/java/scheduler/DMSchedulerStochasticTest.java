package scheduler;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.oristool.simulator.samplers.ExponentialSampler;
import org.oristool.simulator.samplers.UniformSampler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.NoTraceLogger;

class DMSchedulerStochasticTest {

    private final Random random = new Random();

    @RepeatedTest(10)
    @DisplayName("Robustness: Multiple tasks with Uniform and Exponential samplers")
    void stochasticExecutionShouldBeStable() {
        Task[] tasks = new Task[5];
        for (int i=0; i < 5; i++)
            tasks[i] = new Task(
                new UniformSampler(new BigDecimal(40), new BigDecimal(60)),
                60.0,
                new ExponentialSampler(new BigDecimal(5)));
        TaskSet taskSet = new TaskSet(tasks);
        Scheduler scheduler = new DMScheduler(taskSet, 1000.0, new NoTraceLogger());
        scheduler.analyze();
    }

    @RepeatedTest(5)
    @DisplayName("Robustness: High task density with random load")
    void highDensityTaskSetShouldNotBreakEngine() {
        int numTasks = 15;
        Task[] tasks = new Task[numTasks];
        for (int i = 0; i < numTasks; i++) {
            double meanPeriod = 10.0 + random.nextDouble() * 90.0;
            double meanExec = 1.0 + random.nextDouble() * 2.0;
            tasks[i] = new Task(
                new ExponentialSampler(new BigDecimal(meanPeriod)),
                meanPeriod,
                new UniformSampler(new BigDecimal(0.5), new BigDecimal(meanExec)));
        }
        TaskSet taskSet = new TaskSet(tasks);
        Scheduler scheduler = new DMScheduler(
            taskSet,
            500.0,
            new NoTraceLogger());
        scheduler.analyze();
    }
}
