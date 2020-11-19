package com.visma.of.rp.routeevaluator.evaluation.objectives;

public class TimeWindowObjectiveFunction extends CustomCriteriaObjectiveFunction {

    public TimeWindowObjectiveFunction() {
        super(objectiveInfo -> !objectiveInfo.isDestination(),
                (objectiveInfo -> (double) Math.max(0, objectiveInfo.getVisitEnd() -
                        objectiveInfo.getTask().getEndTime())));
    }
}