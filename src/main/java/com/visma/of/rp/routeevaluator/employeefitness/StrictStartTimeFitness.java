package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;
import routeplanner.solvers.fitness.entities.Visit;

public class StrictStartTimeFitness extends EmployeeFitnessValue {

    public static final String ID = "StrictTaskStartTimes";

    public StrictStartTimeFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getStrictTasksStartTimeWeight());
    }

    private StrictStartTimeFitness(EmployeeFitnessValue copy) {
        super(copy);
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
        if (marginalFitnessInfo.isStrict()) {
            double value = findTimePenalty(marginalFitnessInfo.getTask(), marginalFitnessInfo.getArrivalTime());
            return super.getPunishWeight() * value;
        } else
            return 0;
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new StrictStartTimeFitness(this);
    }

    private double findTimePenalty(RouteSimulatorResult result) {
        return findTimePenaltyFor(result);
    }

    private double findTimePenaltyFor(RouteSimulatorResult result) {
        double totalValue = 0.0;
        for (Visit visit : result.extractStrictVisits()) {
            totalValue += findTimePenalty(visit.getTask(), visit.getStart());
        }
        return totalValue;
    }


    private static double findTimePenalty(Task task, double actualStartTime) {
        return Math.max(0.0, (actualStartTime - task.getLatestStartTime()));
    }
}
