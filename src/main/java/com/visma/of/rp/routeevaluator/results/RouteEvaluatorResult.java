package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.algorithm.IRouteEvaluatorObjective;

import java.util.List;

public class RouteEvaluatorResult<T extends ITask> {

    private final Route<T> route;
    private final IRouteEvaluatorObjective objective;

    public RouteEvaluatorResult(IRouteEvaluatorObjective objective, Route<T> route) {
        this.route = route;
        this.objective = objective;
    }

    public Route<T> getRoute() {
        return route;
    }

    public List<Visit<T>> getVisitSolution() {
        return route.getVisitSolution();
    }

    public double getObjectiveValue() {
        return objective.getObjectiveValue();
    }

    public Integer getTimeOfArrivalAtDestination() {
        return route.getRouteFinishedAtTime();
    }

    public IRouteEvaluatorObjective getObjective() {
        return objective;
    }
}
