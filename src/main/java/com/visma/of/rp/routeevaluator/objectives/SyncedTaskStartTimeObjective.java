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
        if (!objectiveInfo.isSynced())
            return 0;
        else {
            System.out.println();
            System.out.println("objectiveInfo.getStartOfServiceNextTask(): " +objectiveInfo.getStartOfServiceNextTask() );
            System.out.println("objectiveInfo.getSyncedTaskLatestStartTime(): " +objectiveInfo.getSyncedTaskLatestStartTime() );
            return Math.max(0.0, (objectiveInfo.getStartOfServiceNextTask() -
                    (objectiveInfo.getSyncedTaskLatestStartTime() + allowedSlack)));
        }
    }
}