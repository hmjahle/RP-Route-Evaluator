package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;

public class TimeWindowObjectiveFunction implements IObjectiveIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {

        if (objectiveInfo.isDestination())
            return 0;
        else
            return Math.max(0, objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime());
    }
}