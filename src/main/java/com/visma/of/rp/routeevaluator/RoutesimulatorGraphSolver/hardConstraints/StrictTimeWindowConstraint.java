package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.task.Task;

public class StrictTimeWindowConstraint extends HardConstraintIncrementalAbstract {

    final long threshold;

    public StrictTimeWindowConstraint(ProblemInstance problemInstance) {
        threshold = problemInstance.getConfiguration().getStrictTaskFinalizerRemoveThresholdSeconds();
    }

    @Override
    protected boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime,
                                           Task task, long serviceStartTime, long syncedStartTime) {
        if (task == null || !task.isStrict()) //Task is office or is not strict.
            return true;
        return serviceStartTime + task.getDurationSeconds() <= task.getEndTime() + threshold;
    }
}
