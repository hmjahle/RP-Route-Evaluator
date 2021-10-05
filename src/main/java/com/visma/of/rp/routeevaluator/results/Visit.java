package com.visma.of.rp.routeevaluator.results;

public class Visit<T> {

    private final T task;
    private final int startTime;
    private final int travelTime;

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
