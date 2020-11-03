package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;

import java.util.ArrayList;
import java.util.List;


public class IncrementalObjectivesHandler {
    private List<IObjectiveIntraRoute> intraShiftObjectives;

    public IncrementalObjectivesHandler() {
        intraShiftObjectives = new ArrayList<>();
    }

    public void addObjectiveIntraShift(IObjectiveIntraRoute objectiveIntraShift) {
        intraShiftObjectives.add(objectiveIntraShift);
    }

    public double calculateIncrementalObjectiveValue(ObjectiveInfo objectiveInfo) {
        double incrementalObjectiveValue = 0.0;
        for (IObjectiveIntraRoute intraShiftObjective : intraShiftObjectives)
            incrementalObjectiveValue += intraShiftObjective.calculateIncrementalObjectiveValueFor(objectiveInfo);
        return incrementalObjectiveValue;
    }
}
