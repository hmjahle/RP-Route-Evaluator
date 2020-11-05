package com.visma.of.rp.routeevaluator.test.objectives;

import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;
import com.visma.of.rp.routeevaluator.test.intraRouteEvaluationInfo.ObjectiveInfo;

import java.util.ArrayList;
import java.util.List;


public class ObjectivesIntraRouteHandler {
    private List<IObjectiveIntraRoute> objectives;

    public ObjectivesIntraRouteHandler() {
        objectives = new ArrayList<>();
    }

    public void addObjectiveIntraShift(IObjectiveIntraRoute objectiveIntraShift) {
        objectives.add(objectiveIntraShift);
    }

    public double calculateIncrementalObjectiveValue(ObjectiveInfo objectiveInfo) {
        double objectiveValue = 0.0;
        for (IObjectiveIntraRoute objective : objectives)
            objectiveValue += objective.calculateIncrementalObjectiveValueFor(objectiveInfo);
        return objectiveValue;
    }
}
