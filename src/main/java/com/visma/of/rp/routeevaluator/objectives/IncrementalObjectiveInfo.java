package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public class IncrementalObjectiveInfo {
    private double travelTime;
    private ITask task;
    private double visitEnd;
    private double arrivalTime;
    private double syncedTaskLatestStartTime;
    private double endOfWorkShift;

    public IncrementalObjectiveInfo(double travelTime, ITask task, double visitEnd, double arrivalTime, double syncedTaskLatestStartTime, double endOfWorkShift) {
        this.travelTime = travelTime;
        this.task = task;
        this.visitEnd = visitEnd;
        this.arrivalTime = arrivalTime;
        this.syncedTaskLatestStartTime = syncedTaskLatestStartTime;
        this.endOfWorkShift = endOfWorkShift;
    }

    public boolean isOfficeTask() {
        return task == null;
    }

    public double getEndOfWorkShift() {
        return endOfWorkShift;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public ITask getTask() {
        return task;
    }

    public boolean isStrict() {
        return task != null && task.isStrict();
    }

    public boolean isSynced() {
        return task != null && task.isSynced();
    }

    public double getVisitEnd() {
        return visitEnd;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getSyncedTasksLatestStartTime() {
        return syncedTaskLatestStartTime;
    }
}
