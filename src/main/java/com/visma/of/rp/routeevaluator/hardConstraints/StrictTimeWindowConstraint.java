package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;


import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public class StrictTimeWindowConstraint extends HardConstraintIncrementalAbstract {

    final long threshold;


    public StrictTimeWindowConstraint() {
        threshold = 60;
    }

    @Override
    protected boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime,
                                           ITask task, long serviceStartTime, long syncedStartTime) {
        if (task == null || !task.isStrict()) //Task is office or is not strict.
            return true;
        return serviceStartTime + task.getDurationSeconds() <= task.getEndTime() + threshold;
    }
}
