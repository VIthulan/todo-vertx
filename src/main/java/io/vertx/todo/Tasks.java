package io.vertx.todo;

import java.util.concurrent.atomic.AtomicInteger;

public class Tasks {
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private int id = 0;
    private boolean isDone;
    private String task;

    public Tasks(String task,boolean isDone){
        this.isDone = isDone;
        this.task = task;
        id = COUNTER.getAndIncrement();
    }

    public Tasks(){
        this.id=COUNTER.getAndIncrement();
    }

    public void setTask(String task){this.task=task;}
    public void setIsDone(boolean isDone){this.isDone=isDone;}

    public String getTask(){return this.task;}
    public boolean getIsDone(){return isDone;}
    public int getId(){return id;}
}
