package com.visma.of.rp.routeevaluator.intraRouteEvaluation.hardConstraints;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;

/**
 * Class that contain the necessary info to evaluate the constraints.
 */
public class ConstraintInfo {

    long endOfShift;
    long earliestPossibleReturnToOfficeTime;
    ITask task;
    long serviceStartTime;
    long syncedStartTime;

    public ConstraintInfo(long endOfShift, long earliestPossibleReturnToOfficeTime, ITask task, long serviceStartTime, long syncedStartTime) {
        this.endOfShift = endOfShift;
        this.earliestPossibleReturnToOfficeTime = earliestPossibleReturnToOfficeTime;
        this.task = task;
        this.serviceStartTime = serviceStartTime;
        this.syncedStartTime = syncedStartTime;
    }

    public long getEndOfShift() {
        return endOfShift;
    }

    public long getEarliestPossibleReturnToOfficeTime() {
        return earliestPossibleReturnToOfficeTime;
    }

    public ITask getTask() {
        return task;
    }

    public boolean isStrict() {
        return task != null && task.isStrict();
    }


    public long getServiceStartTime() {
        return serviceStartTime;
    }

    public long getSyncedStartTime() {
        return syncedStartTime;
    }

}
