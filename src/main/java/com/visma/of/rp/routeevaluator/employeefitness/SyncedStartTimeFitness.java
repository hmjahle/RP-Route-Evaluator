package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;
import routeplanner.solvers.fitness.entities.Visit;

public class SyncedStartTimeFitness extends EmployeeFitnessValue {

    public static final String ID = "syncedTaskStartTimes";

    private long syncedTaskStartTimeSlack;

    public SyncedStartTimeFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getSyncedTasksStartTimeWeight());
        syncedTaskStartTimeSlack = problemInstance.getConfiguration().getSyncedTaskFinalizerRemoveThresholdSeconds();
    }

    private SyncedStartTimeFitness(EmployeeFitnessValue copy) {
        super(copy);
        this.syncedTaskStartTimeSlack = ((SyncedStartTimeFitness) copy).syncedTaskStartTimeSlack;
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newValue = findTimePenalty(routeSimulatorResult);
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), newValue);
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newValue = findTimePenalty(routeSimulatorResult);
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), newValue);
    }

    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo) {
        if (marginalFitnessInfo.isSynced()) {
            double value = findTimePenalty(marginalFitnessInfo.getArrivalTime(), marginalFitnessInfo.getSyncedTasksLatestStartTime());
            return super.getPunishWeight() * value;
        } else
            return 0;
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new SyncedStartTimeFitness(this);
    }

    private double findTimePenalty(RouteSimulatorResult result) {
        double totalValue = 0.0;
        for (Visit syncedVisit : result.extractSyncedVisits()) {
            double actualTime = syncedVisit.getStart();
            totalValue += findTimePenalty(actualTime, result.getChromosomeStartTime(syncedVisit.getTask())
                    + syncedVisit.getTask().getSyncedWithIntervalDiffSeconds());
        }
        return totalValue;
    }

    private double findTimePenalty(double actualStartTime, double latestStartTime) {
        return Math.max(0.0, (actualStartTime - (latestStartTime + syncedTaskStartTimeSlack)));
    }

}
