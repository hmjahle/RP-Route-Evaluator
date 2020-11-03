package com.visma.of.rp.routeevaluator.intraRouteEvaluation.objectives;

import com.visma.of.rp.routeevaluator.PublicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.intraRouteEvaluation.RouteEvaluationInfoAbstract;

public class ObjectiveInfo extends RouteEvaluationInfoAbstract {
    private long travelTime;
    private long visitEnd;
    private long arrivalTime;
    private long syncedTaskLatestStartTime;

    public ObjectiveInfo(long travelTime, ITask task, long visitEnd, long arrivalTime, long syncedTaskLatestStartTime, long endOfWorkShift) {
        super(task,endOfWorkShift);
        this.travelTime = travelTime;
        this.visitEnd = visitEnd;
        this.arrivalTime = arrivalTime;
        this.syncedTaskLatestStartTime = syncedTaskLatestStartTime;
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

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getSyncedTasksLatestStartTime() {
        return syncedTaskLatestStartTime;
    }
}
