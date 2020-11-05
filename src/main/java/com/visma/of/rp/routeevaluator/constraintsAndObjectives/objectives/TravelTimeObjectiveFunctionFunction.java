package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

public class TravelTimeObjectiveFunctionFunction implements IObjectiveFunctionIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        return objectiveInfo.getTravelTime();
    }
}