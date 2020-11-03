package com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo;

import com.visma.of.rp.routeevaluator.PublicInterfaces.ITask;

/**
 * Class that contain the necessary info and functions to evaluate constraints and objectives.
 */
public class RouteEvaluationInfoAbstract {

    protected ITask task;
    protected long endOfWorkShift;

    public RouteEvaluationInfoAbstract(ITask task,long endOfWorkShift) {
        this.task = task;
        this.endOfWorkShift = endOfWorkShift;
    }

    public ITask getTask() {
        return task;
    }

    public long getEndOfWorkShift() {
        return endOfWorkShift;
    }

    public boolean isStrict() {
        return task != null && task.isStrict();
    }

    public boolean isSynced() {
        return task != null && task.isSynced();
    }

    public boolean isOfficeTask() {
        return task == null;
    }

}
