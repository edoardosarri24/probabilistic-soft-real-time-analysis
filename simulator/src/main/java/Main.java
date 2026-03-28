import java.math.BigDecimal;

import org.oristool.simulator.samplers.UniformSampler;

import exeptions.DeadlineMissedException;
import sampler.ConstantSampler;
import scheduler.DMScheduler;
import scheduler.FixedPriorityScheduler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.log.TraceLogger;

public class Main {
    public static void main(String[] args) {
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(35)),
            35,
            new UniformSampler(new BigDecimal(3.5), new BigDecimal(4.5)));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(50)),
            50,
            new UniformSampler(new BigDecimal(9), new BigDecimal(11)));
        Task task3 = new Task(
            new ConstantSampler(new BigDecimal(80)),
            80,
            new UniformSampler(new BigDecimal(10), new BigDecimal(15)));
        TaskSet taskSet = new TaskSet(task1, task2, task3);
        TraceLogger logger = new TraceLogger();
        FixedPriorityScheduler dm = new DMScheduler(taskSet, 1500, logger);
        try {
            dm.analyze();
        } catch (DeadlineMissedException e) {
            e.printStackTrace();
        }
    }

}
