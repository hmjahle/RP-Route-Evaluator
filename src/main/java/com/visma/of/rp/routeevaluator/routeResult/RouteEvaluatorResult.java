package com.visma.of.rp.routeevaluator.routeResult;

import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.IObjective;

import java.util.List;

public class RouteEvaluatorResult {

    private Route route;
    private IObjective objective;

    public RouteEvaluatorResult(IObjective objective,Route route) {
        this.route = route;
        this.objective = objective;
    }

    public Route getRoute() {
        return route;
    }

    public List<Visit> getVisitSolution() {
        return route.getVisitSolution();
    }

    public double getObjectiveValue() {
        return objective.getObjectiveValue();
    }

    public Long getTimeOfOfficeReturn() {
        return route.getTimeOfArrivalAtDestination();
    }

    public IObjective getObjective() {
        return objective;
    }
}
