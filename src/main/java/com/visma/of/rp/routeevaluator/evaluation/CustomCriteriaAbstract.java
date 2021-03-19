package com.visma.of.rp.routeevaluator.evaluation;

import com.visma.of.rp.routeevaluator.evaluation.info.RouteEvaluationInfoAbstract;

import java.util.function.Function;

/**
 * Is used as abstract for constraints and objectives, it determines under which circumstances they are to be applied.
 */
public class CustomCriteriaAbstract {

    private final Function<RouteEvaluationInfoAbstract, Boolean> criteriaFunction;

    public CustomCriteriaAbstract(Function<RouteEvaluationInfoAbstract, Boolean> criteriaFunction) {
        this.criteriaFunction = criteriaFunction;
    }

    protected boolean criteriaIsFulfilled(RouteEvaluationInfoAbstract info) {
        return criteriaFunction.apply(info);
    }


}