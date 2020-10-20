package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.hardConstraints;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.task.Task;

import java.util.ArrayList;
import java.util.List;

public class HardConstraintsIncremental {

    private List<HardConstraintIncrementalAbstract> hardConstraints;

    public HardConstraintsIncremental() {
        hardConstraints = new ArrayList<>();
    }

    public HardConstraintsIncremental(ProblemInstance problemInstance) {
        hardConstraints = new ArrayList<>();
        if (problemInstance.getOfficeInfo().getOvertimeIsHardConstraint())
            this.hardConstraints.add(new OvertimeConstraint());
        if (problemInstance.getOfficeInfo().getStrictTimeWindowsIsHardConstraint())
            this.hardConstraints.add(new StrictTimeWindowConstraint(problemInstance));
        if (problemInstance.getOfficeInfo().getSyncedStartTimeIsHardConstraint())
            this.hardConstraints.add(new SyncedTasksConstraint(problemInstance));
    }

    public boolean isFeasible(long endOfShift, long earliestPossibleReturnToOfficeTime, Task task, long serviceStartTime, long syncedStartTime) {
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
