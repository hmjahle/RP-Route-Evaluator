package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.evaluation.info.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;

/**
 * Objective evaluating how much the synced tasks are delay from the synced task start time plus a potential slack from
 * a master task and a general slack (which is 0 by default).
 */
public class SyncedTasksObjective implements IObjectiveFunctionIntraRoute {

    private final long allowedSlack;

    public SyncedTasksObjective(long allowedSlack) {
        this.allowedSlack = allowedSlack;
    }

    public SyncedTasksObjective() {
        allowedSlack = 0;
    }


    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (!objectiveInfo.isSynced())
            return 0;
        else {
            long latestArrival = objectiveInfo.getSyncedTaskStartTime()
                    + objectiveInfo.getTask().getSyncedWithIntervalDiff()
                    + allowedSlack;
            long arrival = objectiveInfo.getStartOfServiceNextTask();
            return Math.max(0, arrival - latestArrival);
        }
    }
}
