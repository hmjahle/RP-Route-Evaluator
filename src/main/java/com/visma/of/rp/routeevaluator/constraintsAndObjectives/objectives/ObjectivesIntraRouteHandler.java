package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

import java.util.ArrayList;
import java.util.List;


public class ObjectivesIntraRouteHandler {

    private List<IObjectiveFunctionIntraRoute> objectiveFunctions;

    public ObjectivesIntraRouteHandler() {
        objectiveFunctions = new ArrayList<>();
    }

    public void addIntraShiftObjectiveFunction(IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.add(objectiveIntraShift);
    }

    public double calculateIncrementalObjectiveValue(ObjectiveInfo objectiveInfo) {
        double objectiveValue = 0.0;
        for (IObjectiveFunctionIntraRoute objective : objectiveFunctions)
            objectiveValue += objective.calculateIncrementalObjectiveValueFor(objectiveInfo);
        return objectiveValue;
    }
}
