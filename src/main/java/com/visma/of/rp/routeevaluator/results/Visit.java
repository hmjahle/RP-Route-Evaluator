package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class Visit {

    private final ITask task;
    private final int startTime;
    private final int travelTime;

    public Visit(ITask task, int startTime, int travelTime) {
        this.task = task;
        this.startTime = startTime;
        this.travelTime = travelTime;
    }

    public ITask getTask() {
        return task;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getTravelTime() {
        return travelTime;
    }

}
