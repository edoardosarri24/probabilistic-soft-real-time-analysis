package taskSet;

import java.util.Set;

public final class TaskSet {

    private final Set<Task> tasks;

    // Constructor
    public TaskSet(Task... tasks) {
        this.tasks = Set.of(tasks);
    }

    // Getter and Setter
    public Set<Task> getTasks() {
        return this.tasks;
    }

}
