package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

//import probleminstance.entities.timeinterval.task.Task;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public abstract class HardConstraintIncrementalAbstract {

    protected abstract boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTim,
                                                    ITask task, long serviceStartTime, long syncedStartTime);
}
