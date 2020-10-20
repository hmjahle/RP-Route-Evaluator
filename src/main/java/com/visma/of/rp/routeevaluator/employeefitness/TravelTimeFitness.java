package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;


public class TravelTimeFitness extends EmployeeFitnessValue {

    public static final String ID = "TravelTime";

    public TravelTimeFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getTravelTimeWeight()
                * problemInstance.getOfficeInfo().getTravelTimeProportionNormalized());
    }

    private TravelTimeFitness(EmployeeFitnessValue copy) {
        super(copy);
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), routeSimulatorResult.getTotalTravelTimeIncludingParkingAndRobustness());
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), routeSimulatorResult.getTotalTravelTimeIncludingParkingAndRobustness());
    }

    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo fitnessInfo) {
        return super.getPunishWeight() * fitnessInfo.getTravelTime();
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new TravelTimeFitness(this);
    }
}
