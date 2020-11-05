package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

public class SyncedTaskStartTimeObjectiveFunctionFunction implements IObjectiveFunctionIntraRoute {

    private final long allowedSlack;

    public SyncedTaskStartTimeObjectiveFunctionFunction(long allowedSlack) {
        this.allowedSlack = allowedSlack;
    }

    public SyncedTaskStartTimeObjectiveFunctionFunction() {
        this.allowedSlack = 0;
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (!objectiveInfo.isSynced())
            return 0;
        else {
            return Math.max(0.0, (objectiveInfo.getStartOfServiceNextTask() -
                    (objectiveInfo.getSyncedTaskLatestStartTime() + allowedSlack)));
        }
    }
}