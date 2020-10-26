package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;


import com.visma.of.rp.routeevaluator.Interfaces.ITask;

import java.util.ArrayList;
import java.util.List;

public class HardConstraintsIncremental {

    private List<HardConstraintIncrementalAbstract> hardConstraints;

    public HardConstraintsIncremental() {
        hardConstraints = new ArrayList<>();
    }

    public boolean isFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, ITask task, long serviceStartTime, long syncedStartTime) {
        for (HardConstraintIncrementalAbstract constraint : hardConstraints) {
            if (!constraint.constraintIsFeasible(endOfShift, earliestPossibleReturnToOfficeTime, task, serviceStartTime, syncedStartTime)) {
                return false;
            }
        }
        return true;
    }

    public void addHardConstraint(HardConstraintIncrementalAbstract constraint) {
        hardConstraints.add(constraint);
    }
}
