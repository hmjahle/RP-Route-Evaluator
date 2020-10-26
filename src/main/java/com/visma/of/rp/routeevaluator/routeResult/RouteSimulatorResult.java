package com.visma.of.rp.routeevaluator;

import probleminstance.entities.address.Patient;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.solvers.fitness.entities.TravelInfo;
import routeplanner.solvers.fitness.entities.Visit;

import java.util.*;
import java.util.stream.Collectors;

public class RouteSimulatorResult {

    private EmployeeWorkShift employeeWorkShift;
    private List<Visit> visitSolution;
    private Map<Task, Visit> taskToAssignedVisit;
    private Map<Task, Long> syncedTaskToChromosomeStartTime;
    private Long totalTravelTime;
    private Long totalTravelTimeIncludingParkingAndRobustness;
    private Long totalTimeToPatients;
    private Long totalTimeInOffice;
    private Long timeOfOfficeReturn;
    private double totalFitness;

    public RouteSimulatorResult(EmployeeWorkShift employeeWorkShift) {
        this.employeeWorkShift = employeeWorkShift;
        this.visitSolution = new ArrayList<>();
        this.taskToAssignedVisit = new HashMap<>();
        this.syncedTaskToChromosomeStartTime = new HashMap<>();
        resetDataStructureForEmployee();
    }

    RouteSimulatorResult(RouteSimulatorResult copy) {
        this.employeeWorkShift = copy.employeeWorkShift;
        this.visitSolution = new ArrayList<>(copy.visitSolution);
        this.taskToAssignedVisit = new HashMap<>(copy.taskToAssignedVisit);
        this.syncedTaskToChromosomeStartTime = new HashMap<>(copy.syncedTaskToChromosomeStartTime);
        this.totalTravelTime = copy.totalTravelTime;
        this.totalTravelTimeIncludingParkingAndRobustness = copy.totalTravelTimeIncludingParkingAndRobustness;
        this.totalTimeToPatients = copy.totalTimeToPatients;
        this.totalTimeInOffice = copy.totalTimeInOffice;
        this.timeOfOfficeReturn = copy.timeOfOfficeReturn;
        this.totalFitness = copy.totalFitness;
    }

    private void resetDataStructureForEmployee() {
        totalTravelTime = 0L;
        totalTravelTimeIncludingParkingAndRobustness = 0L;
        totalTimeToPatients = 0L;
        totalTimeInOffice = 0L;
        totalFitness = 0;
    }

    public void addVisits(Visit[] visits, int visitsCnt, TravelInfo travelInfo) {
        for (int i = 0; i < visitsCnt; i++) {
            addVisitToVisitSolution(visits[i]);
            updateEmployeeTimeTrackers(visits[i]);
        }
        updateTravelTimes(travelInfo);
    }

    public void addChromosomeStartTime(Task task, long startTime) {
        syncedTaskToChromosomeStartTime.put(task, startTime);
    }

    public long getChromosomeStartTime(Task task) {
        return syncedTaskToChromosomeStartTime.get(task);
    }

    public void setTotalFitness(double totalFitness) {
        this.totalFitness = totalFitness;
    }

    public void updateTimeOfOfficeReturn(Long timeOfOfficeReturn) {
        this.timeOfOfficeReturn = timeOfOfficeReturn;
    }

    public void updateTimeOfOfficeReturn(Long timeOfOfficeReturn, TravelInfo travelInfo) {
        updateTravelTimes(travelInfo);
        this.timeOfOfficeReturn = timeOfOfficeReturn;
    }

    private void addVisitToVisitSolution(Visit visit) {
        visitSolution.add(visit);
        taskToAssignedVisit.put(visit.getTask(), visit);
    }

    private void updateTravelTimes(TravelInfo travelInfo) {
        totalTravelTime += travelInfo.getRawTravelTime();
        totalTravelTimeIncludingParkingAndRobustness += travelInfo.getTravelTimeWithParking();
    }

    private void updateEmployeeTimeTrackers(Visit visit) {
        if (visit.getTask().getAddressEntity() instanceof Patient) {
            totalTimeToPatients += visit.getTask().getDurationSeconds();
        } else {
            totalTimeInOffice += visit.getTask().getDurationSeconds();
        }
    }

    public EmployeeWorkShift getEmployeeWorkShift(){
        return employeeWorkShift;
    }

    public List<Visit> getVisitSolution() {
        return visitSolution;
    }

    public Map<Task, Visit> getTaskToAssignedVisit() {
        return taskToAssignedVisit;
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

    public double getTotalFitness() {
        return totalFitness;
    }

    public Long getTotalTravelTimeIncludingParkingAndRobustness() {
        return totalTravelTimeIncludingParkingAndRobustness;
    }

    public Long getTimeOfOfficeReturn() {
        return timeOfOfficeReturn;
    }

    public Long calculateAndGetTotalFreeTime() {
        long workShiftDuration = employeeWorkShift.getDurationSeconds();
        return Math.max(0, workShiftDuration - totalTimeToPatients - totalTimeInOffice - totalTravelTimeIncludingParkingAndRobustness);
    }

    public List<Task> extractEmployeeRoute() {
        return this.getVisitSolution().stream().map(Visit::getTask).collect(Collectors.toList());
    }

    public Collection<Visit> extractSyncedVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isSynced()).collect(Collectors.toSet());
    }

    public Collection<Visit> extractStrictVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isStrict()).collect(Collectors.toList());
    }
}
