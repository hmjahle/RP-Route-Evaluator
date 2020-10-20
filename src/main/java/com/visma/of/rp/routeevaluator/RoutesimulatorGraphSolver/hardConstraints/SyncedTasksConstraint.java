package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.task.Task;

public class SyncedTasksConstraint extends HardConstraintIncrementalAbstract {

    final long syncedStartTimeSlack;

    public SyncedTasksConstraint(ProblemInstance problemInstance) {
        this.syncedStartTimeSlack = problemInstance.getConfiguration().getSyncedTaskFinalizerRemoveThresholdSeconds();

    }

    @Override
    protected boolean constraintIsFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, Task task,
                                           long serviceStartTime, long syncedStartTime) {
        if (task == null || !task.isSynced()) //Task is office or is not synced.
            return true;
        return (serviceStartTime <= syncedStartTime + syncedStartTimeSlack);
    }
}
