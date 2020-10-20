package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;

public class EmployeeOvertimeFitness extends EmployeeFitnessValue {

    public static final String ID = "EmployeeOvertime";
    private double employeeOvertimeFixedPenaltyForExceedingEndOfShift;
    private double employeeOvertimePenaltyPerSecond;
    private double employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing;

    public EmployeeOvertimeFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getEmployeeOvertimeWeight());
        this.employeeOvertimeFixedPenaltyForExceedingEndOfShift =
                problemInstance.getConfiguration().getEmployeeOvertimeFixedPenaltyForExceedingEndOfShift();
        this.employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing =
                problemInstance.getConfiguration().getEmployeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing();
        this.employeeOvertimePenaltyPerSecond =
                problemInstance.getConfiguration().getEmployeeOvertimePenaltyPerSecond();
    }

    private EmployeeOvertimeFitness(EmployeeOvertimeFitness copy) {
        super(copy);
        this.employeeOvertimeFixedPenaltyForExceedingEndOfShift = copy.employeeOvertimeFixedPenaltyForExceedingEndOfShift;
        this.employeeOvertimePenaltyPerSecond = copy.employeeOvertimePenaltyPerSecond;
        this.employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing = copy.employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing;
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {

        double newValue = findOvertimePenalty(routeSimulatorResult);
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), newValue);
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newValue = findOvertimePenalty(routeSimulatorResult);
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), newValue);
    }

    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo) {
        if (marginalFitnessInfo.getTask() != null) //Task is office task
            return 0;
        return calculateOverTimeValue(marginalFitnessInfo.getEndOfWorkShift(), marginalFitnessInfo.getArrivalTime(), employeeOvertimeFixedPenaltyForExceedingEndOfShift,
                employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing, employeeOvertimePenaltyPerSecond) * super.getPunishWeight();
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new EmployeeOvertimeFitness(this);
    }

    private double calculateOverTimeValue(double endOfWorkShift, double timeOfReturnToOffice, double employeeOvertimeFixedPenaltyForExceedingEndOfShift,
                                          double employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing, double employeeOvertimePenaltyPerSecond) {
        double penalty = 0.0;
        double diffFromShiftEnd = timeOfReturnToOffice - endOfWorkShift;
        if (diffFromShiftEnd > 0)
            penalty = employeeOvertimeFixedPenaltyForExceedingEndOfShift;

        if (diffFromShiftEnd + employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing > 0) {
            penalty += Math.min(diffFromShiftEnd + employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing,
                    employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing) * employeeOvertimePenaltyPerSecond;
        }
        return penalty;
    }

    private double findOvertimePenalty(RouteSimulatorResult routeSimulatorResult) {
        long endOfWorkShift = routeSimulatorResult.getEmployeeWorkShift().getEndTime();
        long timeOfReturnToOffice = routeSimulatorResult.getTimeOfOfficeReturn();
        return calculateOverTimeValue(endOfWorkShift, timeOfReturnToOffice, employeeOvertimeFixedPenaltyForExceedingEndOfShift,
                employeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing, employeeOvertimePenaltyPerSecond);
    }
}
