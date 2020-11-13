package com.visma.of.rp.routeevaluator.routeResult;

import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Route {

    private IShift employeeWorkShift;
    private List<Visit> visitSolution;
    private long timeOfOfficeReturn;

    public void setTimeOfOfficeReturn(long timeOfOfficeReturn) {
        this.timeOfOfficeReturn = timeOfOfficeReturn;
    }

    public Route(IShift employeeWorkShift) {
        this.employeeWorkShift = employeeWorkShift;
        this.visitSolution = new ArrayList<>();
        this.timeOfOfficeReturn = 0L;
    }

    Route(Route copy) {
        this.visitSolution = new ArrayList<>(copy.visitSolution);
        this.employeeWorkShift = copy.employeeWorkShift;
        this.timeOfOfficeReturn = copy.timeOfOfficeReturn;
    }

    public void addVisits(Visit[] visits, int visitsCnt) {
        for (int i = 0; i < visitsCnt; i++) {
            addVisitToVisitSolution(visits[i]);
        }
    }

    public void updateTimeOfOfficeReturn(long timeOfOfficeReturn) {
        this.timeOfOfficeReturn = timeOfOfficeReturn;
    }

    private void addVisitToVisitSolution(Visit visit) {
        visitSolution.add(visit);
    }

    public IShift getEmployeeWorkShift() {
        return employeeWorkShift;
    }

    public List<Visit> getVisitSolution() {
        return visitSolution;
    }

    public long getTimeOfOfficeReturn() {
        return timeOfOfficeReturn;
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
}
