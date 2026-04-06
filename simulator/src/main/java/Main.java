import java.io.IOException;

import config.ConfigLoader;
import config.SimulationConfig;
import scheduler.DeadlineMonotonicScheduler;
import scheduler.Scheduler;
import taskSet.TaskSet;
import utils.MyUtils;
import utils.collector.TaskExecutionTimeCollector;

public class Main {
    public static void main(String[] args) {
        String configPath = args.length > 0 ? args[0] : "src/config.yaml";
        try {
            SimulationConfig config = ConfigLoader.loadConfig(configPath);
            TaskSet taskSet = config.toTaskSet();
            Scheduler dm = new DeadlineMonotonicScheduler(
                taskSet,
                config.simulationDurationMs,
                config.deadlineMissStrategy.toStrategy());
            TaskExecutionTimeCollector dataSimulation = dm.analyze();
            MyUtils.callPythonExtractor(dataSimulation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
