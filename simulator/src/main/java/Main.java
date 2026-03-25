import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.oristool.simulator.samplers.UniformSampler;

import exeptions.DeadlineMissedException;
import scheduler.DMScheduler;
import scheduler.Scheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Task task1 = new Task(
            35,
            35,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(2), new BigDecimal(2))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(1), new BigDecimal(1.5))),
                new Chunk(
                    3,
                    new UniformSampler(new BigDecimal(0.5), new BigDecimal(1)))));
        Task task2 = new Task(
            50,
            50,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(3), new BigDecimal(4))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(3), new BigDecimal(3.5))),
                new Chunk(
                    3,
                    new UniformSampler(new BigDecimal(3), new BigDecimal(3.5)))));
        Task task3 = new Task(
            80,
            80,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(4), new BigDecimal(5))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(4), new BigDecimal(4.5)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3));
        Scheduler rm = new DMScheduler(taskSet, 1500);
        rm.scheduleDataset(2);
    }

}