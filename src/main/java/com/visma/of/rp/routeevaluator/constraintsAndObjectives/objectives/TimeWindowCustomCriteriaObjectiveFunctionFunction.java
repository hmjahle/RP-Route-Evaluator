package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.TimeWindowCustomCriteriaAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.function.Function;

public class TimeWindowCustomCriteriaObjectiveFunctionFunction extends TimeWindowCustomCriteriaAbstract implements IObjectiveFunctionIntraRoute {

    public TimeWindowCustomCriteriaObjectiveFunctionFunction(Function<ITask, Boolean> criteriaFunction) {
        super(criteriaFunction, 0);
    }

    public TimeWindowCustomCriteriaObjectiveFunctionFunction(Function<ITask, Boolean> criteriaFunction, long allowedSLack) {
        super(criteriaFunction, allowedSLack);
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isDestination() || !criteriaIsFulfilled(objectiveInfo))
            return 0;
        else
            return Math.max(0, objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime());
    }
}