package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

import probleminstance.entities.timeinterval.task.Task;

public abstract class HardConstraintIncrementalAbstract {

    protected abstract boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTim,
                                                    Task task, long serviceStartTime, long syncedStartTime);
}
