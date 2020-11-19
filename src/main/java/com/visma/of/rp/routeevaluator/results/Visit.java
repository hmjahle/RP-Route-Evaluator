package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class Visit {

    private final ITask task;
    private long startTime;
    private long endTime;
    private long travelTime;

    public Visit(ITask task, long startTime, long endTime, long travelTime) {
        this.task = task;
        this.startTime = startTime;
        this.endTime = endTime;
        this.travelTime = travelTime;
    }

    public ITask getTask() {
        return task;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTravelTime() {
        return travelTime;
    }

}
