package com.visma.of.rp.routeevaluator.routeResult;


import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;

import java.util.*;
import java.util.stream.Collectors;


public class RouteEvaluatorResult {

    private IShift employeeWorkShift;
    private List<Visit> visitSolution;
    private Long totalTravelTime;
    private Long totalTimeToPatients;
    private Long totalTimeInOffice;
    private Long timeOfOfficeReturn;
    private double objectiveValue;

    public RouteEvaluatorResult(IShift employeeWorkShift) {
        this.employeeWorkShift = employeeWorkShift;
        this.visitSolution = new ArrayList<>();
        resetDataStructureForEmployee();
    }

    RouteEvaluatorResult(RouteEvaluatorResult copy) {
        this.employeeWorkShift = copy.employeeWorkShift;
        this.visitSolution = new ArrayList<>(copy.visitSolution);
        this.totalTravelTime = copy.totalTravelTime;
        this.totalTimeToPatients = copy.totalTimeToPatients;
        this.totalTimeInOffice = copy.totalTimeInOffice;
        this.timeOfOfficeReturn = copy.timeOfOfficeReturn;
        this.objectiveValue = copy.objectiveValue;
    }

    private void resetDataStructureForEmployee() {
        totalTravelTime = 0L;
        totalTimeToPatients = 0L;
        totalTimeInOffice = 0L;
        objectiveValue = 0;
    }

    public void updateTotalTravelTime(long totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }

    public void addVisits(Visit[] visits, int visitsCnt) {
        for (int i = 0; i < visitsCnt; i++) {
            addVisitToVisitSolution(visits[i]);
        }
    }

    public void setObjectiveValue(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public void updateTimeOfOfficeReturn(Long timeOfOfficeReturn) {
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

    public Long getTotalTravelTime() {
        return totalTravelTime;
    }

    public Long getTotalTimeToPatients() {
        return totalTimeToPatients;
    }

    public Long getTotalTimeInOffice() {
        return totalTimeInOffice;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public Long getTimeOfOfficeReturn() {
        return timeOfOfficeReturn;
    }

    public Long calculateAndGetTotalFreeTime() {
        long workShiftDuration = employeeWorkShift.getDuration();
        return Math.max(0, workShiftDuration - totalTimeToPatients - totalTimeInOffice - totalTravelTime);
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
