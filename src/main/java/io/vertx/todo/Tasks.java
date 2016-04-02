package io.vertx.todo;

/**
 * Task object that will be used to save parameters
 */
public class Tasks {
    private boolean completed;
    private String title;
    private int order = 0;

    public Tasks(String task, boolean completed) {
        this.completed = completed;
        this.title = task;
    }

    public Tasks() {
    }

    public void setOrder(int order){
        this.order=order;
    }
    public int getOrder(){
        return order;
    }
    public void setTitle(String task) {
        this.title = task;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean getCompleted() {
        return completed;
    }

}
