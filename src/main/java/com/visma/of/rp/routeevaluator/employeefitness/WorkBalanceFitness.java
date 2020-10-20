package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;

public class WorkBalanceFitness extends EmployeeFitnessValue {

    public static final String ID = "WorkBalance";

    public WorkBalanceFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getWorkBalanceWeight()
                * (problemInstance.getOfficeInfo().getUseWorkBalance() ? problemInstance.getConfiguration().getWorkBalanceWeight() : 0));
    }

    private WorkBalanceFitness(EmployeeFitnessValue employeeFitnessValue) {
        super(employeeFitnessValue);
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
        return 0;
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new WorkBalanceFitness(this);
    }

    private double findTimePenalty(RouteSimulatorResult routeSimulatorResult){
        long totalDrivingTime = routeSimulatorResult.getTotalTravelTime();
        long totalVisitTime   = routeSimulatorResult.getTotalTimeToPatients();
        long totalOfficeTime  = routeSimulatorResult.getTotalTimeInOffice();
        long totalPatientWorkTime    = totalDrivingTime + totalVisitTime;
        long totalShiftTimeAvailableForPatients = routeSimulatorResult.getEmployeeWorkShift()
                .getDurationSeconds() - totalOfficeTime;
        double thresholdShift = totalShiftTimeAvailableForPatients
                * problemInstance.getConfiguration().getWorkBalanceLowerWorkThresholdPercentage();
        return Math.max(thresholdShift - totalPatientWorkTime , 0);
    }


}
