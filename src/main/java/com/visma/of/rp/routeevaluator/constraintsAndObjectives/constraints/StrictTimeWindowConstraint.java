package com.visma.of.rp.routeevaluator.test.constraints;


import com.visma.of.rp.routeevaluator.test.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;

public class StrictTimeWindowConstraint implements IConstraintIntraRoute {

    public StrictTimeWindowConstraint(long allowedSlack) {
        this.allowedSlack = allowedSlack;
    }

    final long allowedSlack;

    public StrictTimeWindowConstraint() {
        allowedSlack = 0;
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (!constraintInfo.isStrict()) //Task is office or is not strict.
            return true;
        return constraintInfo.getStartOfServiceNextTask() + constraintInfo.getTask().getDuration() <= constraintInfo.getTask().getEndTime() + allowedSlack;
    }
}
