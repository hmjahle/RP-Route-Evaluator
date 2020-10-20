package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;
import routeplanner.solvers.fitness.entities.Visit;

import java.util.List;

public class TimeWindowHighFitness extends EmployeeFitnessValue {

    public static final String ID = "TimeWindowPatientTaskHigh";

    public TimeWindowHighFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getTimeWindowForVisitWeightHigh() *
                problemInstance.getOfficeInfo().getTimeWindowProportionNormalized());
    }

    private TimeWindowHighFitness(TimeWindowHighFitness copy) {
        super(copy);
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double value = calculateValueOf(routeSimulatorResult.getVisitSolution());
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), value);
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double value = calculateValueOf(routeSimulatorResult.getVisitSolution());
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), value);
    }

    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo) {
        if (marginalFitnessInfo.isOfficeTask())
            return 0;
        return super.getPunishWeight() * Math.max(marginalFitnessInfo.getVisitEnd() - marginalFitnessInfo.getTask().getEndTime() - marginalFitnessInfo.getTask().getLowToHighThreshold(), 0);
    }


    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new TimeWindowHighFitness(this);
    }

    private double calculateValueOf(List<Visit> visits) {
        double value = 0.0;
        for (Visit visit : visits) {
            value += calculateValueForVisit(visit);
        }
        return value;
    }

    private double calculateValueForVisit(Visit visit) {
        return Math.max(visit.getEnd() - visit.getTask().getEndTime() - visit.getTask().getLowToHighThreshold(), 0);
    }
}
