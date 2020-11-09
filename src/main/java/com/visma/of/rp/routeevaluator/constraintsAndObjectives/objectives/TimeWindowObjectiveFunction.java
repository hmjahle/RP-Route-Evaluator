package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

public class TimeWindowObjectiveFunction extends CustomCriteriaObjectiveFunction {

    public TimeWindowObjectiveFunction() {
        super(objectiveInfo -> !objectiveInfo.isDestination(),
                objectiveInfo -> Math.max(0.0, objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime()));
    }
}