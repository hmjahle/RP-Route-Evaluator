package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.TimeWindowCustomCriteriaAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.function.Function;

public class StrictTimeWindowConstraint extends TimeWindowCustomCriteriaAbstract implements IConstraintIntraRoute {

    public StrictTimeWindowConstraint(Function<ITask, Boolean> criteriaFunction, long allowedSlack) {
        super(criteriaFunction);
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
