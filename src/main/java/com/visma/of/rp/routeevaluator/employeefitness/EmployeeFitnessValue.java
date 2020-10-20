package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;

import java.util.HashMap;
import java.util.Map;

public abstract class EmployeeFitnessValue {

    private double value;
    private final double punishWeight;
    private Map<EmployeeWorkShift, Double> employeeValues;
    protected ProblemInstance problemInstance;

    EmployeeFitnessValue(ProblemInstance problemInstance, double punishWeight) {
        this.value = 0.0;
        this.punishWeight = punishWeight;
        this.problemInstance = problemInstance;
        initializeEmployeeValues();
    }

    EmployeeFitnessValue(EmployeeFitnessValue copy) {
        this.value = copy.value;
        this.punishWeight = copy.punishWeight;
        this.problemInstance = copy.problemInstance;
        this.employeeValues = new HashMap<>(copy.employeeValues);
    }


    public abstract double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual);

    public abstract double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual);

    public abstract double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo);

    public abstract EmployeeFitnessValue cloneEmployeeFitnessValue();

    private void initializeEmployeeValues() {
        this.employeeValues = new HashMap<>();
        for (EmployeeWorkShift employeeWorkShift : problemInstance.getProblemInstanceHelpStructure().getEmployeeWorkShiftList()) {
            this.employeeValues.put(employeeWorkShift, 0.0);
        }
    }

    public Double getEmployeeValue(EmployeeWorkShift employeeWorkShift) {
        return employeeValues.get(employeeWorkShift);
    }

    public double getValue() {
        return value;
    }

    public double getFitness(EmployeeWorkShift employeeWorkShift) {
        return employeeValues.get(employeeWorkShift) * this.punishWeight;
    }

    public double getFitness() {
        return this.value * this.punishWeight;
    }

    double setValue(EmployeeWorkShift employeeWorkShift, double newEmployeeValue) {
        double oldEmployeeValue = this.employeeValues.get(employeeWorkShift);
        double deltaTotalValue = newEmployeeValue - oldEmployeeValue;
        this.employeeValues.put(employeeWorkShift, newEmployeeValue);
        this.value += deltaTotalValue;
        return deltaTotalValue * this.punishWeight;
    }

    double getDeltaFitness(EmployeeWorkShift employeeWorkShift, double newEmployeeValue) {
        double oldEmployeeValue = this.employeeValues.get(employeeWorkShift);
        double deltaTotalValue = newEmployeeValue - oldEmployeeValue;
        return deltaTotalValue * this.punishWeight;
    }

    public double getPunishWeight() {
        return punishWeight;
    }
}
