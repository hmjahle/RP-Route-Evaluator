package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.CustomCriteriaAbstract;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.RouteEvaluationInfoAbstract;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

import java.util.function.Function;

/**
 * A custom objective function takes a criteria which is when the objective is applied and an objective function
 * which is the objective that is applied.
 */
public class CustomCriteriaObjectiveFunction extends CustomCriteriaAbstract implements IObjectiveFunctionIntraRoute {

    private final Function<ObjectiveInfo, Double> objectiveFunction;


    public CustomCriteriaObjectiveFunction(Function<RouteEvaluationInfoAbstract, Boolean> criteriaFunction, Function<ObjectiveInfo, Double> objectiveFunction
        ) {
        super(criteriaFunction);
        this.objectiveFunction = objectiveFunction;
    }

    @Override
    public double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo) {
        if (!criteriaIsFulfilled(objectiveInfo))
            return 0;
        else
            return objectiveFunction.apply(objectiveInfo);
    }
}