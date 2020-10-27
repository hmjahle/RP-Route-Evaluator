package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.Interfaces.IObjectiveIntraShift;

import java.util.ArrayList;
import java.util.List;


public class IncrementalObjectivesHandler {
    private List<IObjectiveIntraShift> intraShiftObjectives;

    public IncrementalObjectivesHandler() {
        intraShiftObjectives = new ArrayList<>();
    }

    public void addObjectiveIntraShift(IObjectiveIntraShift objectiveIntraShift) {
        intraShiftObjectives.add(objectiveIntraShift);
    }

    public double calculateIncrementalObjectiveValue(IncrementalObjectiveInfo incrementalObjectiveInfo) {
        double incrementalObjectiveValue = 0.0;
        for (IObjectiveIntraShift intraShiftObjective : intraShiftObjectives)
            incrementalObjectiveValue += intraShiftObjective.getIncrementalObjectiveValueFor(incrementalObjectiveInfo);
        return incrementalObjectiveValue;
    }
}
