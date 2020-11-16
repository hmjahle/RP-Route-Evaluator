package com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo;

import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

/**
 * Class that contain the necessary info to evaluate the constraints.
 */
public class ConstraintInfo extends RouteEvaluationInfoAbstract {

    long earliestOfficeReturn;
    long startOfServiceNextTask;

    public ConstraintInfo(long endOfWorkShift, long earliestOfficeReturn, ITask task, long startOfServiceNextTask, long syncedTaskLatestStartTime) {
        super(task, endOfWorkShift, syncedTaskLatestStartTime);
        this.earliestOfficeReturn = earliestOfficeReturn;
        this.startOfServiceNextTask = startOfServiceNextTask;
        this.syncedTaskStartTime = syncedTaskLatestStartTime;
    }

    public long getEarliestOfficeReturn() {
        return earliestOfficeReturn;
    }

    public boolean isStrict() {
        return task != null && task.isStrict();
    }


    public long getStartOfServiceNextTask() {
        return startOfServiceNextTask;
    }


}