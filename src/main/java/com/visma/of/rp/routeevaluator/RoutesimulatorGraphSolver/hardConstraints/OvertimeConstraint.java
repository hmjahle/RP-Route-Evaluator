package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

import probleminstance.entities.timeinterval.task.Task;

import static routeplanner.finalizer.RemoveOvertimeTasksAndUpdateVisits.isOverTime;

public class OvertimeConstraint extends HardConstraintIncrementalAbstract {

    @Override
    protected boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, Task task,
                                           long serviceStartTime, long syncedStartTime) {
        return !isOverTime(endOfShift, earliestPossibleReturnToOfficeTime);
    }
}
