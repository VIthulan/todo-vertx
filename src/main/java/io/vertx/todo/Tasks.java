package io.vertx.todo;

/**
 * Task object that will be used to save parameters
 */
public class Tasks {
    private boolean completed;
    private String task;

    public Tasks(String task, boolean completed) {
        this.completed = completed;
        this.task = task;
    }

    public Tasks() {
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTask() {
        return this.task;
    }

    public boolean getCompleted() {
        return completed;
    }

}
