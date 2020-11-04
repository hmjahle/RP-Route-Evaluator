package com.visma.of.rp.routeevaluator.constraints;


import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

public class StrictTimeWindowConstraint implements IConstraintIntraRoute {

    final long threshold;

    public StrictTimeWindowConstraint() {
        threshold = 60;
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (!constraintInfo.isStrict()) //Task is office or is not strict.
            return true;
        return constraintInfo.getStartOfServiceNextTask() + constraintInfo.getTask().getDuration() <= constraintInfo.getTask().getEndTime() + threshold;
    }
}
