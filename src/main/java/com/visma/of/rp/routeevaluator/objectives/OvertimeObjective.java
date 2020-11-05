package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;

public class OvertimeObjective implements IObjectiveIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isDestination()) {
            long workShiftEnd = objectiveInfo.getEndOfWorkShift();
            long officeReturn = objectiveInfo.getStartOfServiceNextTask();
            if (isOverTime(workShiftEnd, officeReturn)) {
                return officeReturn - workShiftEnd;
            }
        }
        return 0;
    }

    private boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }
}