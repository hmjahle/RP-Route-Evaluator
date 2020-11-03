package com.visma.of.rp.routeevaluator.hardConstraints;

import com.visma.of.rp.routeevaluator.Interfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public class OvertimeConstraint implements IConstraintIntraRoute {

    @Override
    public boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, ITask task,
                                        long serviceStartTime, long syncedStartTime) {
        return !isOverTime(endOfShift, earliestPossibleReturnToOfficeTime);
    }

    private boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }
}
