package com.visma.of.rp.routeevaluator.evaluation.info;

import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;

/**
 * Class that contain the necessary info to evaluate the constraints.
 */
public class ConstraintInfo extends RouteEvaluationInfoAbstract {

    short shiftId;
    long earliestOfficeReturn;
    long startOfServiceNextTask;

    public ConstraintInfo(IShift employeeWorkShift, long earliestOfficeReturn, ITask task, long startOfServiceNextTask, long syncedTaskLatestStartTime) {
        super(task, employeeWorkShift.getEndTime(), syncedTaskLatestStartTime);
        this.shiftId = employeeWorkShift.getId();
        this.earliestOfficeReturn = earliestOfficeReturn;
        this.startOfServiceNextTask = startOfServiceNextTask;
        this.syncedTaskStartTime = syncedTaskLatestStartTime;
    }

    public long getEarliestOfficeReturn() {
        return earliestOfficeReturn;
    }

    public long getStartOfServiceNextTask() {
        return startOfServiceNextTask;
    }


}
