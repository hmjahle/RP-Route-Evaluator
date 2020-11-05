package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;

public class TravelTimeObjective implements IObjectiveIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        return objectiveInfo.getTravelTime();
    }
}