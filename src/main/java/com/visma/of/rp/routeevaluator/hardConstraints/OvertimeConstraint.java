package com.visma.of.rp.routeevaluator.hardConstraints;

import com.visma.of.rp.routeevaluator.PublicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

public class OvertimeConstraint implements IConstraintIntraRoute {

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        return !isOverTime(constraintInfo.getEndOfWorkShift(), constraintInfo.getEarliestPossibleReturnToOfficeTime());
    }

    private boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }

}
