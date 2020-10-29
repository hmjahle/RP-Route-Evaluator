package com.visma.of.rp.routeevaluator.routeResult;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public class Visit {

    private final ITask task;
    private long start;
    private long end;
    private long travelTimeWithParking;
    private long robustnessTimeSeconds;

    public Visit(ITask task, long start, long end,
                 long travelTimeWithParking, long robustnessTimeSeconds) {
        this.task = task;
        this.start = start;
        this.end = end;
        this.travelTimeWithParking = travelTimeWithParking;
        this.robustnessTimeSeconds = robustnessTimeSeconds;
    }

    public Visit(Visit copy) {
        this.task = copy.task;
        this.start = copy.start;
        this.end = copy.end;
        this.travelTimeWithParking = copy.travelTimeWithParking;
        this.robustnessTimeSeconds = copy.robustnessTimeSeconds;
    }

    public ITask getTask() {
        return task;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getTravelTimeWithParking() {
        return travelTimeWithParking;
    }

    public long getRobustnessTimeSeconds() {
        return robustnessTimeSeconds;
    }

}
