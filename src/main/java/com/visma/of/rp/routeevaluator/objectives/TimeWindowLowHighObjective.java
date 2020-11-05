package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;

public class TimeWindowLowHighObjective implements IObjectiveIntraRoute {

    /**
     * Cut off at which the high penalty is applied.
     */
    final long highPenaltyCutOff;
    /**
     * Multiplier applied outside the cut off, hence breaking the time window by more than
     * the cut off is penalized x times more, than inside the cut off.
     */
    final double highPenaltyMultiplier;

    public TimeWindowLowHighObjective(long highPenaltyCutOff, double highPenaltyMultiplier) {
        this.highPenaltyCutOff = highPenaltyCutOff;
        this.highPenaltyMultiplier = highPenaltyMultiplier;
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isDestination())
            return 0;
        else {
            long timeWindowBreak = objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime();
            if (timeWindowBreak <= highPenaltyCutOff)
                return Math.max(0, timeWindowBreak);
            else {
                long highPenaltyTimeWindowBreak = timeWindowBreak - highPenaltyCutOff;
                return highPenaltyCutOff + highPenaltyTimeWindowBreak * highPenaltyMultiplier;
            }
        }
    }
}