import java.math.BigDecimal;

import org.oristool.simulator.samplers.ExponentialSampler;

import exeptions.DeadlineMissedException;
import sampler.DeterministicSampler;
import scheduler.DMScheduler;
import scheduler.Scheduler;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyUtils;
import utils.TaskExecutionTimeCollector;
import utils.log.TraceLogger;

public class Main {
    public static void main(String[] args) {
        Task task1 = new Task(
            new DeterministicSampler(new BigDecimal(35)),
            35,
            new DeterministicSampler(new BigDecimal(3)));
        Task task2 = new Task(
            new DeterministicSampler(new BigDecimal(50)),
            50,
            new DeterministicSampler(new BigDecimal(3)));
        Task task3 = new Task(
            new DeterministicSampler(new BigDecimal(80)),
            80,
            new ExponentialSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(task1, task2, task3);
        Scheduler dm = new DMScheduler(
            taskSet,
            5000000,
            new TraceLogger());
        try {
            TaskExecutionTimeCollector dataSimulation = dm.analyze();
            MyUtils.callPythonExtractor(dataSimulation);
        } catch (DeadlineMissedException e) {
            e.printStackTrace();
        }
    }
}
