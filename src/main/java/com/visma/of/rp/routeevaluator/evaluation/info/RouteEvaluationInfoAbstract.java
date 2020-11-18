package com.visma.of.rp.routeevaluator.evaluation.info;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

/**
 * Class that contain the necessary info and functions to evaluate constraints and objectives.
 */
public class RouteEvaluationInfoAbstract {

    protected ITask task;
    protected long endOfWorkShift;
    protected long syncedTaskStartTime;

    public RouteEvaluationInfoAbstract(ITask task, long endOfWorkShift, long syncedTaskStartTime) {
        this.task = task;
        this.endOfWorkShift = endOfWorkShift;
        this.syncedTaskStartTime = syncedTaskStartTime;
    }

    public long getSyncedTaskStartTime() {
        return syncedTaskStartTime;
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
