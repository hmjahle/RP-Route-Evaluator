package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public class OvertimeConstraint extends HardConstraintIncrementalAbstract {

    @Override
    protected boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, ITask task,
                                           long serviceStartTime, long syncedStartTime) {
        return !isOverTime(endOfShift, earliestPossibleReturnToOfficeTime);
    }

    private boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }
}
