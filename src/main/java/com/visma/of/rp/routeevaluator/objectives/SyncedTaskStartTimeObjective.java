package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;

public class SyncedTaskStartTimeObjective implements IObjectiveIntraRoute {

    final long allowedSlack;

    public SyncedTaskStartTimeObjective(long allowedSlack) {
        this.allowedSlack = allowedSlack;
    }

    public SyncedTaskStartTimeObjective() {
        this.allowedSlack = 0;
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        return -1;
    }
}