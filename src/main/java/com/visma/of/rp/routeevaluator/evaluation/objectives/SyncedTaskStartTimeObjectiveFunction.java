package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

public class SyncedTaskStartTimeObjectiveFunction implements IObjectiveFunctionIntraRoute {

    private final long allowedSlack;

    public SyncedTaskStartTimeObjectiveFunction(long allowedSlack) {
        this.allowedSlack = allowedSlack;
    }

    public SyncedTaskStartTimeObjectiveFunction() {
        this.allowedSlack = 0;
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (!objectiveInfo.isSynced())
            return 0;
        else {
            return Math.max(0.0, (objectiveInfo.getStartOfServiceNextTask() -
                    (objectiveInfo.getSyncedTaskStartTime() + allowedSlack
                            + objectiveInfo.getTask().getSyncedWithIntervalDiff())));
        }
    }
}