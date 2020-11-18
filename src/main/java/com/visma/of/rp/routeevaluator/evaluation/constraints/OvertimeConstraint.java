package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.OvertimeAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;

public class OvertimeConstraint extends OvertimeAbstract implements IConstraintIntraRoute {

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        return !isOverTime(constraintInfo.getEndOfWorkShift(), constraintInfo.getEarliestOfficeReturn());
    }

}
