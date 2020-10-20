package com.visma.of.rp.routeevaluator;

import aws.entity.responses.routesinformation.KeyNumbers;
import aws.entity.responses.routesinformation.RouteKeyNumbers;
import probleminstance.entities.address.Patient;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.employeefitness.*;
import routeplanner.solvers.fitness.entities.Visit;

public class FitnessHelper {


    public static KeyNumbers getTotalKeyNumbers(Individual individual) {
        double travelTime = getFitnessValueIfExists(individual.getFitness(), TravelTimeFitness.ID);
        double overQualified = getFitnessValueIfExists(individual.getFitness(), OverqualifiedFitness.ID);
        double timeWindow = getFitnessValueIfExists(individual.getFitness(), TimeWindowLowFitness.ID)
                + getFitnessValueIfExists(individual.getFitness(), TimeWindowHighFitness.ID);
        double visitHistory = getTotalVisitHistory(individual.getFitness());
        long totalPatientTime = getTotalPatientTimeForAllEmployees(individual);
        long totalFreeTime = getTotalFreeTime(individual.getFitness());
        NonAllocatedTasksIndividual nonAllocatedTasksIndividual = individual.getChromosome().createNonAllocatedTasksIndividual();
        int totalUnallocated = nonAllocatedTasksIndividual.getTotalNumberOfNonAllocatedTasks();
        int unallocatedFromSearch = nonAllocatedTasksIndividual.getNumberOfNonAllocatedFromSearch();
        return new KeyNumbers(individual.getFitness().getTotalFitness(), travelTime, timeWindow, visitHistory, totalPatientTime, totalFreeTime, overQualified, totalUnallocated, unallocatedFromSearch);
    }

    private static double getTotalVisitHistory(Fitness fitness) {
        double total = 0.0;
        for (EmployeeWorkShift employeeWorkShift : fitness.getIndividual().getChromosome().getEmployeeWorkShiftsInChromosome()) {
            total += getNumberOfPatientsVisitedByEmployeeNotInPrimaryGroupForEmployee(fitness, employeeWorkShift);
        }
        return total;
    }

    private static long getTotalFreeTime(Fitness fitness) {
        long total = 0L;
        for (EmployeeWorkShift employeeWorkShift : fitness.getIndividual().getChromosome().getEmployeeWorkShiftsInChromosome()) {
            total += getTotalFreeTime(fitness, employeeWorkShift);
        }
        return total;
    }

    public static RouteKeyNumbers getTotalKeyNumbersForEmployeeWorkShift(Fitness fitness, EmployeeWorkShift employeeWorkShift) {
        double employeeFitness = fitness.getSingleEmployeeTotalFitness(employeeWorkShift);
        double travelTime = getFitnessValueIfExists(fitness, employeeWorkShift, TravelTimeFitness.ID);
        double overQualified = getFitnessValueIfExists(fitness, employeeWorkShift, OverqualifiedFitness.ID);
        double timeWindow = getFitnessValueIfExists(fitness, employeeWorkShift, TimeWindowLowFitness.ID) + getFitnessValueIfExists(fitness, employeeWorkShift, TimeWindowHighFitness.ID);
        double visitHistory = getNumberOfPatientsVisitedByEmployeeNotInPrimaryGroupForEmployee(fitness, employeeWorkShift);
        long totalPatientTime = getTotalPatientTime(fitness.getEmployeeRouteSimulatorResultsFor(employeeWorkShift));
        long totalFreeTime = getTotalFreeTime(fitness, employeeWorkShift);
        return new RouteKeyNumbers(employeeFitness, travelTime, timeWindow, visitHistory, totalPatientTime, totalFreeTime, overQualified,
                employeeWorkShift.getWorkShiftId(), employeeWorkShift.getOriginalEmployeeId());
    }

    private static long getTotalPatientTimeForAllEmployees(Individual individual) {
        long total = 0L;
        for (EmployeeWorkShift employeeWorkShift : individual.getChromosome().getEmployeeWorkShiftsInChromosome()) {
            total += getTotalPatientTime(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift));
        }
        return total;
    }

    private static long getTotalPatientTime(RouteSimulatorResult routeSimulatorResult) {
        return routeSimulatorResult.getTotalTimeToPatients();
    }

    private static long getTotalFreeTime(Fitness fitness, EmployeeWorkShift employeeWorkShift) {
        RouteSimulatorResult routeSimulatorResult = fitness.getEmployeeRouteSimulatorResultsFor(employeeWorkShift);
        return routeSimulatorResult.calculateAndGetTotalFreeTime();
    }

    private static double getNumberOfPatientsVisitedByEmployeeNotInPrimaryGroupForEmployee(Fitness fitness, EmployeeWorkShift employeeWorkShift) {
        double counter = 0.0;
        if (fitness.getEmployeeFitnessValues().containsKey(VisitHistoryFitness.ID)) {
            String originalEmployeeId = employeeWorkShift.getOriginalEmployeeId();
            for (Visit visit : fitness.getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution()) {
                if (visit.getTask().getAddressEntity() instanceof Patient) {
                    String patientId = visit.getTask().getAddressEntity().getId();
                    boolean shouldAdd = fitness.getIndividual().getProblemInstance().getProblemInstanceHelpStructure()
                            .getVisitHistoryFitnessCreator().getVisitHistoryPatientInformationFor(patientId).shouldCountEmployeeAsNewVisit(originalEmployeeId);
                    if (shouldAdd) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    private static double getFitnessValueIfExists(Fitness fitness, String fitnessKey) {
        if (fitness.getEmployeeFitnessValues().containsKey(fitnessKey)) {
            return fitness.getEmployeeFitnessValues().get(fitnessKey).getValue();
        }
        return 0.0;
    }

    private static double getFitnessValueIfExists(Fitness fitness, EmployeeWorkShift employeeWorkShift, String fitnessKey) {
        if (fitness.getEmployeeFitnessValues().containsKey(fitnessKey)) {
            return fitness.getEmployeeFitnessValues().get(fitnessKey).getEmployeeValue(employeeWorkShift);
        }
        return 0.0;
    }


}
