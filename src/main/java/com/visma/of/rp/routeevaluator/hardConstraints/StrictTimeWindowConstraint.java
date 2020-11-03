package com.visma.of.rp.routeevaluator.hardConstraints;


import com.visma.of.rp.routeevaluator.PublicInterfaces.IConstraintIntraRoute;
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
        return constraintInfo.getServiceStartTime() + constraintInfo.getTask().getDuration() <= constraintInfo.getTask().getEndTime() + threshold;
    }
}
