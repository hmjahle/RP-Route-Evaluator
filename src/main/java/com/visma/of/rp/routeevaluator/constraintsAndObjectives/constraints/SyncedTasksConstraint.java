package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
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
        if (!constraintInfo.isSynced())
            return true;
        return (constraintInfo.getStartOfServiceNextTask() <= constraintInfo.getSyncedTaskLatestStartTime() + allowedSlack);
    }
}
