package com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo;

import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

public class ObjectiveInfo extends RouteEvaluationInfoAbstract {
    private long travelTime;
    private long visitEnd;
    private long startOfServiceNextTask;

    public ObjectiveInfo(long travelTime, ITask task, long visitEnd, long startOfServiceNextTask, long syncedTaskLatestStartTime, long endOfWorkShift) {
        super(task, endOfWorkShift, syncedTaskLatestStartTime);
        this.travelTime = travelTime;
        this.visitEnd = visitEnd;
        this.startOfServiceNextTask = startOfServiceNextTask;
    }

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

}
