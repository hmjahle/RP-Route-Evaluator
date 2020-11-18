package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.evaluation.OvertimeAbstract;
import com.visma.of.rp.routeevaluator.evaluation.info.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;

public class OvertimeObjectiveFunction extends OvertimeAbstract implements IObjectiveFunctionIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isDestination()) {
            long workShiftEnd = objectiveInfo.getEndOfWorkShift();
            long officeReturn = objectiveInfo.getStartOfServiceNextTask();
            if (isOverTime(workShiftEnd, officeReturn)) {
                return (double) (officeReturn - workShiftEnd);
            }
        }
        return 0.0;
    }

}