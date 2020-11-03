package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.PublicInterfaces.IObjectiveIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;

public class TimeWindowObjective implements IObjectiveIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isDestination())
            return 0;
        else
            return Math.max(0,objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime());
    }
}