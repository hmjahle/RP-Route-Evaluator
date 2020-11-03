package com.visma.of.rp.routeevaluator.hardConstraints;


import com.visma.of.rp.routeevaluator.Interfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;

public class StrictTimeWindowConstraint implements IConstraintIntraRoute {

    final long threshold;


    public StrictTimeWindowConstraint() {
        threshold = 60;
    }

    @Override
    public boolean constraintIsFeasible(ConstraintInfo constraintInfo) {
        if (task == null || !task.isStrict()) //Task is office or is not strict.
            return true;
        return serviceStartTime + task.getDuration() <= task.getEndTime() + threshold;
    }
}
