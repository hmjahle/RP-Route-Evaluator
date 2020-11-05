package com.visma.of.rp.routeevaluator.test.intraRouteEvaluationInfo;

import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

/**
 * Class that contain the necessary info and functions to evaluate constraints and objectives.
 */
public class RouteEvaluationInfoAbstract {

    protected ITask task;
    protected long endOfWorkShift;
    protected long syncedTaskLatestStartTime;

    public RouteEvaluationInfoAbstract(ITask task, long endOfWorkShift, long syncedTaskLatestStartTime) {
        this.task = task;
        this.endOfWorkShift = endOfWorkShift;
        this.syncedTaskLatestStartTime = syncedTaskLatestStartTime;
    }

    public long getSyncedTaskLatestStartTime() {
        return syncedTaskLatestStartTime;
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

    public boolean isDestination() {
        return task == null;
    }

}
