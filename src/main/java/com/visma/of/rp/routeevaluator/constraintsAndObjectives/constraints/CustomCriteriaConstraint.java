package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.CustomCriteriaAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.RouteEvaluationInfoAbstract;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;

import java.util.function.Function;

/**
 * A custom constraint function takes a criteria which is when the constraint is applied and a constraint which is the
 * constraint that is applied.
 */
public class CustomCriteriaConstraint extends CustomCriteriaAbstract implements IConstraintIntraRoute {

    private final Function<ConstraintInfo, Boolean> constraint;

    public CustomCriteriaConstraint(Function<RouteEvaluationInfoAbstract, Boolean> criteriaFunction,
                                    Function<ConstraintInfo, Boolean> constraint) {
        super(criteriaFunction);
        this.constraint = constraint;
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (!criteriaIsFulfilled(constraintInfo))
            return true;
        return constraint.apply(constraintInfo);
    }
}
