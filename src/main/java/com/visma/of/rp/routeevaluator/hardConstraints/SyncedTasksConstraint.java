package com.visma.of.rp.routeevaluator.hardConstraints;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;


public class SyncedTasksConstraint extends HardConstraintIncrementalAbstract {

    final long syncedStartTimeSlack;

    public SyncedTasksConstraint(){
        syncedStartTimeSlack = 0;
    }

    @Override
    protected boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, ITask task,
                                           long serviceStartTime, long syncedStartTime) {
        if (task == null || !task.isSynced()) //Task is office or is not synced.
            return true;
        return (serviceStartTime <= syncedStartTime + syncedStartTimeSlack);
    }
}
