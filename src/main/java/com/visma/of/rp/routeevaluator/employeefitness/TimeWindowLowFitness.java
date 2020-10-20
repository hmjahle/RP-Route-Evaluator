package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;
import routeplanner.solvers.fitness.entities.Visit;

import java.util.List;

public class TimeWindowLowFitness extends EmployeeFitnessValue {

    public static final String ID = "TimeWindowPatientTaskLow";


    public TimeWindowLowFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getTimeWindowForVisitWeightLow()
                * problemInstance.getOfficeInfo().getTimeWindowWeightProportion());
    }

    private TimeWindowLowFitness(EmployeeFitnessValue copy) {
        super(copy);
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double value = calculateValue(routeSimulatorResult.getVisitSolution());
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), value);
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double value = calculateValue(routeSimulatorResult.getVisitSolution());
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), value);
    }

    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo) {
        if (marginalFitnessInfo.isOfficeTask())
            return 0;
        return calculateFitnessValueFor(marginalFitnessInfo.getTask(), marginalFitnessInfo.getVisitEnd()) * super.getPunishWeight();
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new TimeWindowLowFitness(this);
    }

    private double calculateValue(List<Visit> visits) {
        double value = 0.0;
        for (Visit visit : visits) {
            value += calculateFitnessValueFor(visit.getTask(), visit.getEnd());
        }
        return value;
    }

    private double calculateFitnessValueFor(Task task, double visitEnd) {
        double windowEnd = task.getEndTime();
        double thresholdValue = task.getLowToHighThreshold();
        double diff = visitEnd - windowEnd;
        return Math.max(Math.min(thresholdValue, diff), 0);
    }


}
