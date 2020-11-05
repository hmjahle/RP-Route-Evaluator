package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.TimeWindowCustomCriteriaAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.function.Function;

public class TimeWindowCustomCriteriaConstraint extends TimeWindowCustomCriteriaAbstract implements IConstraintIntraRoute {

    public TimeWindowCustomCriteriaConstraint(Function<ITask, Boolean> criteriaFunction) {
        super(criteriaFunction);
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (constraintInfo.isDestination() || !criteriaIsFulfilled(constraintInfo))
            return true;
        return constraintInfo.getStartOfServiceNextTask() + constraintInfo.getTask().getDuration() <= constraintInfo.getTask().getEndTime();
    }
}
