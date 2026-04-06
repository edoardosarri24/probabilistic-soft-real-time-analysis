package config;

import taskSet.Task;

public class TaskConfig {

    public SamplerConfig period;
    public double deadlineMs;
    public SamplerConfig executionTime;
    public SamplerConfig firstReleaseTime;

    public Task toTask() {
        if (firstReleaseTime == null) {
            return new Task(period.toSampler(), deadlineMs, executionTime.toSampler());
        } else {
            return new Task(period.toSampler(), deadlineMs, executionTime.toSampler(), firstReleaseTime.toSampler());
        }
    }

}
