package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.solver.algorithm.IObjective;

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

    public Integer getTimeOfArrivalAtDestination() {
        return route.getRouteFinishedAtTime();
    }

    public IObjective getObjective() {
        return objective;
    }
}
