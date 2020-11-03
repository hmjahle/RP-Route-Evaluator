package com.visma.of.rp.routeevaluator.intraRouteEvaluation.hardConstraints;


import com.visma.of.rp.routeevaluator.PublicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.PublicInterfaces.ITask;

import java.util.ArrayList;
import java.util.List;

public class HardConstraintsIncremental {

    List<IConstraintIntraRoute> hardConstraints;

    public HardConstraintsIncremental() {
        hardConstraints = new ArrayList<>();
    }

    public boolean isFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, ITask task, long serviceStartTime, long syncedStartTime) {
        for (IConstraintIntraRoute constraint : hardConstraints) {
            if (!constraint.constraintIsFeasible(endOfShift, earliestPossibleReturnToOfficeTime, task, serviceStartTime, syncedStartTime)) {
                return false;
            }
        }
        return true;
    }

    void addHardConstraint(IConstraintIntraRoute constraint) {
        hardConstraints.add(constraint);
    }
}
