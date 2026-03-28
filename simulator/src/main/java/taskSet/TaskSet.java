package taskSet;

import java.util.Set;
import utils.MyUtils;

public final class TaskSet {

    private final Set<Task> tasks;

    // Constructor
    public TaskSet(Task... tasks) {
        MyUtils.requireNonNull(tasks, "tasks");
        if (tasks.length == 0)
            throw new IllegalArgumentException("TaskSet cannot be empty");
        this.tasks = Set.of(tasks);
    }

    // Getter and Setter
    public Set<Task> getTasks() {
        return this.tasks;
    }

}
