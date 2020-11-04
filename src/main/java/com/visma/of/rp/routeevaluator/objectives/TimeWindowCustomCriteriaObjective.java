package com.visma.of.rp.routeevaluator.objectives;

import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.function.Function;

public class TimeWindowCustomCriteriaObjective implements IObjectiveIntraRoute {

    Function<ITask, Boolean> criteriaFunction;

    public TimeWindowCustomCriteriaObjective(Function<ITask, Boolean> criteriaFunction) {
        this.criteriaFunction = criteriaFunction;
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (objectiveInfo.isDestination() || !criteriaFunction.apply(objectiveInfo.getTask()))
            return 0;
        else
            return Math.max(0, objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime());
    }
}