package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class Visit<T extends ITask> {

    private final T task;
    private int startTime;
    private int travelTime;

    public Visit(T task, int startTime, int travelTime) {
        this.task = task;
        this.startTime = startTime;
        this.travelTime = travelTime;
    }

    public T getTask() {
        return task;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getTravelTime() {
        return travelTime;
    }

}
