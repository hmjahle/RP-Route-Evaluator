package com.visma.of.rp.routeevaluator.test.constraints;

import com.visma.of.rp.routeevaluator.test.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;


public class SyncedTasksConstraint implements IConstraintIntraRoute {

    final long allowedSlack;

    public SyncedTasksConstraint(long allowedSlack) {
        this.allowedSlack = allowedSlack;
    }

    public SyncedTasksConstraint() {
        allowedSlack = 0;
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (!constraintInfo.isSynced()) //Task is office or is not synced.
            return true;
        return (constraintInfo.getStartOfServiceNextTask() <= constraintInfo.getSyncedTaskLatestStartTime() + allowedSlack);
    }
}
