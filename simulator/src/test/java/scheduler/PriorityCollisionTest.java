package scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.oristool.simulator.samplers.Sampler;

import exeptions.DeadlineMissedException;
import sampler.DeterministicSampler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.MyLogger;

public class PriorityCollisionTest {

    @Test
    @DisplayName("Priority Collision: Two tasks with same priority should both execute")
    public void testPriorityCollision() throws DeadlineMissedException {
        Sampler period = new DeterministicSampler(new BigDecimal(100));
        Sampler exec = new DeterministicSampler(new BigDecimal(10));
        Task task1 = new Task(period, 100, exec);
        Task task2 = new Task(period, 100, exec);
        // Manual priority assignment to simulate collision
        task1.setPriority(1);
        task2.setPriority(1);
        TaskSet ts = new TaskSet(task1, task2);
        StringBuilder logOutput = new StringBuilder();
        MyLogger logger = new MyLogger() {
            @Override
            public void log(String message) {
                logOutput.append(message);
            }
            @Override
            public void close() {}
        };
        FixedPriorityScheduler scheduler = new FixedPriorityScheduler(ts, 50, logger) {
            @Override
            protected void assignPriority() {
                // Priorities already set manually
            }
        };
        scheduler.analyze();
        String output = logOutput.toString();
        assertThat(output).contains("execute Task 1");
        assertThat(output).contains("execute Task 2");
    }
}
