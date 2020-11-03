package com.visma.of.rp.routeevaluator.intraRouteEvaluation.hardConstraints;

import com.visma.of.rp.routeevaluator.PublicInterfaces.IConstraintIntraRoute;


public class SyncedTasksConstraint implements IConstraintIntraRoute {

    final long syncedStartTimeSlack;

    public SyncedTasksConstraint() {
        syncedStartTimeSlack = 60;
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (!constraintInfo.isSynced()) //Task is office or is not synced.
            return true;
        return (constraintInfo.serviceStartTime <= constraintInfo.syncedStartTime + syncedStartTimeSlack);
    }
}
