import java.math.BigDecimal;

import org.oristool.simulator.samplers.UniformSampler;

import exeptions.DeadlineMissedException;
import scheduler.DMScheduler;
import scheduler.Scheduler;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Task task1 = new Task(
            35,
            35,
            new UniformSampler(new BigDecimal(3.5), new BigDecimal(4.5)));
        Task task2 = new Task(
            50,
            50,
            new UniformSampler(new BigDecimal(9), new BigDecimal(11)));
        Task task3 = new Task(
            80,
            80,
            new UniformSampler(new BigDecimal(8), new BigDecimal(9.5)));
        TaskSet taskSet = new TaskSet(task1, task2, task3);
        Scheduler rm = new DMScheduler(taskSet, 1500);
        rm.analyze();
    }

}