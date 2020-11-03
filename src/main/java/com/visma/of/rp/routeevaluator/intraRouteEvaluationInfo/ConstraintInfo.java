package com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo;

import com.visma.of.rp.routeevaluator.PublicInterfaces.ITask;

/**
 * Class that contain the necessary info to evaluate the constraints.
 */
public class ConstraintInfo extends RouteEvaluationInfoAbstract {

    long earliestPossibleReturnToOfficeTime;
    long serviceStartTime;
    long syncedStartTime;

    public ConstraintInfo(long endOfWorkShift, long earliestPossibleReturnToOfficeTime, ITask task, long serviceStartTime, long syncedStartTime) {
        super(task, endOfWorkShift);
        this.earliestPossibleReturnToOfficeTime = earliestPossibleReturnToOfficeTime;
        this.serviceStartTime = serviceStartTime;
        this.syncedStartTime = syncedStartTime;
    }

    public long getEarliestPossibleReturnToOfficeTime() {
        return earliestPossibleReturnToOfficeTime;
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
