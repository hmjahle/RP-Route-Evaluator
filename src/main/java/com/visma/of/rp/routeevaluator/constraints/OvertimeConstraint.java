package com.visma.of.rp.routeevaluator.constraints;

import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

public class OvertimeConstraint implements IConstraintIntraRoute {

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        return !isOverTime(constraintInfo.getEndOfWorkShift(), constraintInfo.getEarliestOfficeReturn());
    }

    private boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }

}
