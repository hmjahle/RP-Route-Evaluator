package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import probleminstance.entities.timeinterval.task.Task;

public class MarginalFitnessInfo {
    private double travelTime;
    private Task task;
    private double visitEnd;
    private double arrivalTime;
    private double syncedTaskLatestStartTime;
    private double endOfWorkShift;

    public MarginalFitnessInfo(double travelTime, Task task, double visitEnd, double arrivalTime, double syncedTaskLatestStartTime, double endOfWorkShift) {
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

    public Task getTask() {
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
