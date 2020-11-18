package com.visma.of.rp.routeevaluator.evaluation.constraints;

import com.visma.of.rp.routeevaluator.evaluation.OvertimeAbstract;
import com.visma.of.rp.routeevaluator.evaluation.info.ConstraintInfo;
import com.visma.of.rp.routeevaluator.interfaces.IConstraintIntraRoute;

public class OvertimeConstraint extends OvertimeAbstract implements IConstraintIntraRoute {

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        return !isOverTime(constraintInfo.getEndOfWorkShift(), constraintInfo.getEarliestOfficeReturn());
    }

}
