package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.Interfaces.IObjectiveIntraRoute;

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

    public double calculateIncrementalObjectiveValue(IncrementalObjectiveInfo incrementalObjectiveInfo) {
        double incrementalObjectiveValue = 0.0;
        for (IObjectiveIntraRoute intraShiftObjective : intraShiftObjectives)
            incrementalObjectiveValue += intraShiftObjective.calculateIncrementalObjectiveValueFor(incrementalObjectiveInfo);
        return incrementalObjectiveValue;
    }
}
