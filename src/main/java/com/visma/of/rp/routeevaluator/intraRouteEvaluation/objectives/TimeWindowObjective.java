package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.Interfaces.IObjectiveIntraRoute;

public class TimeWindowObjective implements IObjectiveIntraRoute {

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isOfficeTask())
            return 0;
        else
            return Math.max(0,objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime());
    }
}