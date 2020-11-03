package com.visma.of.rp.routeevaluator.intraRouteEvaluation.hardConstraints;

import com.visma.of.rp.routeevaluator.PublicInterfaces.IConstraintIntraRoute;

public class OvertimeConstraint implements IConstraintIntraRoute {

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        return !isOverTime(constraintInfo.getEndOfWorkShift(), constraintInfo.earliestPossibleReturnToOfficeTime);
    }

    private boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }
}
