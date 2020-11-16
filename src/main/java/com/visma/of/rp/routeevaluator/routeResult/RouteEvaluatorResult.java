package com.visma.of.rp.routeevaluator.routeResult;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.IObjective;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RouteEvaluatorResult {

    private Route route;
    private IObjective objective;

    public RouteEvaluatorResult(IShift employeeWorkShift, IObjective objective) {
        route = new Route(employeeWorkShift);
        this.objective = objective;
    }

    public RouteEvaluatorResult(IShift employeeWorkShift) {
        route = new Route(employeeWorkShift);
        objective = new WeightedObjective();
    }

    public void setObjectiveValue(double objectiveValue) {
        objective = new WeightedObjective(objectiveValue);
    }

    public void addVisits(Visit[] visits, int visitsCnt) {
        route.addVisits(visits, visitsCnt);
    }

    public void updateTimeOfOfficeReturn(Long timeOfOfficeReturn) {
        route.setTimeOfOfficeReturn(timeOfOfficeReturn);
    }

    public IShift getEmployeeWorkShift() {
        return route.getEmployeeWorkShift();
    }

    public List<Visit> getVisitSolution() {
        return route.getVisitSolution();
    }


    public double getObjectiveValue() {
        return objective.getObjectiveValue();
    }

    public Long getTimeOfOfficeReturn() {
        return route.getTimeOfOfficeReturn();
    }

    public List<ITask> extractEmployeeRoute() {
        return this.getVisitSolution().stream().map(Visit::getTask).collect(Collectors.toList());
    }

    public Collection<Visit> extractSyncedVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isSynced()).collect(Collectors.toSet());
    }

    public Collection<Visit> extractStrictVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isStrict()).collect(Collectors.toList());
    }


    public IObjective getObjective() {
        return objective;
    }
}
