package com.visma.of.rp.routeevaluator.constraintsAndObjectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.RouteEvaluationInfoAbstract;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.function.Function;

public class TimeWindowCustomCriteriaAbstract {

    private final Function<ITask, Boolean> criteriaFunction;

    public TimeWindowCustomCriteriaAbstract(Function<ITask, Boolean> criteriaFunction) {
        this.criteriaFunction = criteriaFunction;
    }

    protected boolean criteriaIsFulfilled(RouteEvaluationInfoAbstract info) {
        return criteriaFunction.apply(info.getTask());
    }

}