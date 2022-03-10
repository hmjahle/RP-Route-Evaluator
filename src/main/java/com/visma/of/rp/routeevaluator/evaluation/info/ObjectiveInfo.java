package com.visma.of.rp.routeevaluator.evaluation.info;

import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class ObjectiveInfo extends RouteEvaluationInfoAbstract {
    private final long travelTime;
    private final long visitEnd;
    private final long startOfServiceNextTask;

    public ObjectiveInfo(long travelTime, ITask task, long visitEnd, long startOfServiceNextTask, long syncedTaskStartTime, IShift employeeWorkShift) {
        super(task, employeeWorkShift, syncedTaskStartTime);
        this.travelTime = travelTime;
        this.visitEnd = visitEnd;
        this.startOfServiceNextTask = startOfServiceNextTask;
    }

    @Override
    public long getEndOfWorkShift() {
        return endOfWorkShift;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public long getVisitEnd() {
        return visitEnd;
    }

    public long getStartOfServiceNextTask() {
        return startOfServiceNextTask;
    }

    public short getEmployeeWorkShiftId() {
        return shiftId;
    }
}
