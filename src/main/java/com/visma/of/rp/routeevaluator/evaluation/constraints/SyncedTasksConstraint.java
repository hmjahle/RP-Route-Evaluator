package com.visma.of.rp.routeevaluator.evaluation.constraints;

import com.visma.of.rp.routeevaluator.evaluation.info.ConstraintInfo;
import com.visma.of.rp.routeevaluator.interfaces.IConstraintIntraRoute;


public class SyncedTasksConstraint implements IConstraintIntraRoute {

    private final long allowedSlack;

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
        return (constraintInfo.getStartOfServiceNextTask() <= constraintInfo.getSyncedTaskStartTime()
                + constraintInfo.getTask().getSyncedWithIntervalDiff()
                + allowedSlack);
    }
}
