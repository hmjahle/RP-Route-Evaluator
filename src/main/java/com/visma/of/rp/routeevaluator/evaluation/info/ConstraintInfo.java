package com.visma.of.rp.routeevaluator.evaluation.info;

import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;

/**
 * Class that contain the necessary info to evaluate the constraints.
 */
public class ConstraintInfo extends RouteEvaluationInfoAbstract {
    long earliestOfficeReturn;
    long startOfServiceNextTask;
    long shiftStartTime;

    public ConstraintInfo(IShift employeeWorkShift, long earliestOfficeReturn, ITask task, long startOfServiceNextTask, long syncedTaskLatestStartTime, long shiftStartTime) {
        super(task, employeeWorkShift, syncedTaskLatestStartTime);
        this.earliestOfficeReturn = earliestOfficeReturn;
        this.startOfServiceNextTask = startOfServiceNextTask;
        this.syncedTaskStartTime = syncedTaskLatestStartTime;
        this.shiftStartTime = shiftStartTime;
    }

    public long getEarliestOfficeReturn() {
        return earliestOfficeReturn;
    }

    public long getStartOfServiceNextTask() {
        return startOfServiceNextTask;
    }

    public short getShiftId() {
        return shiftId;
    }

    public long getShiftStartTime() {
        return shiftStartTime;
    }
}
