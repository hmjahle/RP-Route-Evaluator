package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.OvertimeAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

public class OvertimeObjectiveFunctionFunction extends OvertimeAbstract implements IObjectiveFunctionIntraRoute {

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

}