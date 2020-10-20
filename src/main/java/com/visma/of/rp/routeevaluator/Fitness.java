package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.problemInstance.ProblemInstance;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.RouteSimulator;
import routeplanner.solvers.fitness.employeefitness.*;
import utils.individual.ValueFromPropertiesAbstract;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Fitness extends ValueFromPropertiesAbstract {

    private Individual individual;
    private Configuration configuration;
    private double totalEmployeeFitness;
    private double unallocatedFitness;
    private Map<EmployeeWorkShift, Double> totalEmployeeFitnessPerEmployee;
    private Map<String, EmployeeFitnessValue> employeeFitnessValues;
    private Map<EmployeeWorkShift, RouteSimulatorResult> employeeRouteSimulatorResults;
    private RouteSimulator routeSimulator;

    public Fitness(RouteSimulator solver, Individual individual) {
        this.routeSimulator = solver;
        this.individual = individual;
        this.configuration = individual.getProblemInstance().getConfiguration();
        this.employeeRouteSimulatorResults = new HashMap<>();
        this.totalEmployeeFitnessPerEmployee = new HashMap<>();
        initialize();
    }

    /**
     * Calculate the total unallocated tasks fitness of the current chromosome.
     *
     * @return Total fitness value.
     */
    public double calculateUnallocatedFitness() {
        return individual.getChromosome().getUnallocatedTasks().size()
                * individual.getProblemInstance().getConfiguration().getUnallocatedTasksFitnessWeight();
    }

    /**
     * Calculate the delta unallocated tasks fitness.
     *
     * @param deltaNumberOfTasksUnallocated Change in the number of unallocated tasks, negative or positive.
     * @return Delta fitness.
     */
    public double calculateDeltaUnallocatedFitness(int deltaNumberOfTasksUnallocated) {
        return deltaNumberOfTasksUnallocated * individual.getProblemInstance().getConfiguration().getUnallocatedTasksFitnessWeight();
    }

    public double getEmployeeFitnessValueForEmployee(EmployeeWorkShift employeeWorkShift, String employeeFitnessValueId) {
        return employeeFitnessValues.get(employeeFitnessValueId).getFitness(employeeWorkShift);
    }

    public double getEmployeeValueForEmployee(EmployeeWorkShift employeeWorkShift, String employeeFitnessValueId) {
        return employeeFitnessValues.get(employeeFitnessValueId).getEmployeeValue(employeeWorkShift);
    }

    public Fitness(Fitness oldFitness, Individual newIndividual) {
        this.individual = newIndividual;
        this.configuration = oldFitness.configuration;
        this.employeeRouteSimulatorResults = new HashMap<>();
        this.totalEmployeeFitnessPerEmployee = new HashMap<>();
        this.totalEmployeeFitness = oldFitness.totalEmployeeFitness;
        for (EmployeeWorkShift employeeWorkShift : oldFitness.employeeRouteSimulatorResults.keySet()) {
            this.employeeRouteSimulatorResults.put(employeeWorkShift,
                    new RouteSimulatorResult(oldFitness.employeeRouteSimulatorResults.get(employeeWorkShift)));
            this.totalEmployeeFitnessPerEmployee.put(employeeWorkShift,
                    oldFitness.totalEmployeeFitnessPerEmployee.get(employeeWorkShift));
        }
        copyFitnessValues(oldFitness);
    }

    private void copyFitnessValues(Fitness oldFitness) {
        this.employeeFitnessValues = oldFitness.copyEmployeeFitnessValues();
        this.totalEmployeeFitness = oldFitness.totalEmployeeFitness;
    }

    private Map<String, EmployeeFitnessValue> copyEmployeeFitnessValues() {
        Map<String, EmployeeFitnessValue> copy = new HashMap<>();
        for (String fitnessValueKey : this.employeeFitnessValues.keySet()) {
            copy.put(fitnessValueKey, this.employeeFitnessValues.get(fitnessValueKey).cloneEmployeeFitnessValue());
        }
        return copy;
    }

    private static Map<String, EmployeeFitnessValue> addEmployeeFitnessClasses(Configuration configuration, ProblemInstance problemInstance) {
        HashMap<String, EmployeeFitnessValue> employeeFitnessValues = new HashMap<>();
        employeeFitnessValues.putAll(addIntraEmployeeFitnessClasses(problemInstance));
        employeeFitnessValues.putAll(addInterEmployeeFitnessClasses(configuration, problemInstance));
        return employeeFitnessValues;
    }

    //Add fitness classes that regards multiple employees
    private static Map<String, EmployeeFitnessValue> addInterEmployeeFitnessClasses(Configuration configuration,
                                                                                    ProblemInstance problemInstance) {
        HashMap<String, EmployeeFitnessValue> employeeFitnessValues = new HashMap<>();
        if (configuration.getVisitHistoryWeight() > 0.0) {
            employeeFitnessValues.put(VisitHistoryFitness.ID,
                    new VisitHistoryFitness(problemInstance));
        }
        if (configuration.getWorkBalanceWeight() > 0.0) {
            employeeFitnessValues.put(WorkBalanceFitness.ID,
                    new WorkBalanceFitness(problemInstance));
        }
        if (configuration.getOverqualifiedFitnessWeight() > 0.0)
            employeeFitnessValues.put(OverqualifiedFitness.ID,
                    new OverqualifiedFitness(problemInstance));
        return employeeFitnessValues;
    }

    //Add fitness measure that is related to a single employee
    static Map<String, EmployeeFitnessValue> addIntraEmployeeFitnessClasses(ProblemInstance problemInstance) {
        Configuration configuration = problemInstance.getConfiguration();
        boolean addStrictTaskFitness = problemInstance.getProblemInstanceHelpStructure().getNumberOfStrictTasks() > 0;
        boolean addSyncedStartTimeFitness = !problemInstance.getProblemInstanceHelpStructure().getSyncedTasks().isEmpty();
        HashMap<String, EmployeeFitnessValue> employeeFitnessValues = new HashMap<>();
        if (configuration.getTravelTimeWeight() > 0.0) {
            employeeFitnessValues.put(TravelTimeFitness.ID,
                    new TravelTimeFitness(problemInstance));
        }
        if (configuration.getTimeWindowForVisitWeightLow() > 0.0) {
            employeeFitnessValues.put(TimeWindowLowFitness.ID,
                    new TimeWindowLowFitness(problemInstance));
        }
        if (configuration.getTimeWindowForVisitWeightHigh() > 0.0) {
            employeeFitnessValues.put(TimeWindowHighFitness.ID,
                    new TimeWindowHighFitness(problemInstance));
        }
        if (configuration.getStrictTasksStartTimeWeight() > 0.0 && addStrictTaskFitness) {
            employeeFitnessValues.put(StrictStartTimeFitness.ID,
                    new StrictStartTimeFitness(problemInstance));
        }
        if (configuration.getSyncedTasksStartTimeWeight() > 0.0 && addSyncedStartTimeFitness) {
            employeeFitnessValues.put(SyncedStartTimeFitness.ID,
                    new SyncedStartTimeFitness(problemInstance));
        }
        if (configuration.getEmployeeOvertimeWeight() > 0.0) {
            employeeFitnessValues.put(EmployeeOvertimeFitness.ID,
                    new EmployeeOvertimeFitness(problemInstance));
        }
        return employeeFitnessValues;
    }

    public double getSingleEmployeeTotalFitness(EmployeeWorkShift employeeWorkShift) {
        return totalEmployeeFitnessPerEmployee.get(employeeWorkShift);
    }

    public double updateEmployeeFitness(RouteSimulatorResult routeSimulatorResult) {
        double deltaFitness = 0.0;
        for (EmployeeFitnessValue fv : this.employeeFitnessValues.values()) {
            deltaFitness += fv.updateFitnessValueForEmployee(routeSimulatorResult, individual);
        }
        this.totalEmployeeFitness += deltaFitness;
        double newValue = this.totalEmployeeFitnessPerEmployee.get(routeSimulatorResult.getEmployeeWorkShift()) + deltaFitness;
        this.totalEmployeeFitnessPerEmployee.put(routeSimulatorResult.getEmployeeWorkShift(), newValue);
        return deltaFitness;
    }

    public double getDeltaEmployeeFitnessWithoutUpdating(RouteSimulatorResult routeSimulatorResult) {
        double deltaFitness = 0.0;
        for (EmployeeFitnessValue fv : this.employeeFitnessValues.values()) {
            deltaFitness += fv.getDeltaFitnessValueForEmployee(routeSimulatorResult, individual);
        }
        return deltaFitness;
    }

    public void initialize() {
        totalEmployeeFitness = 0.0;
        employeeFitnessValues = addEmployeeFitnessClasses(this.configuration, individual.getProblemInstance());
        initializeFitnessDataStructureWithRouteSimulator();
        for (EmployeeWorkShift employeeWorkShift : individual.getChromosome().getEmployeeWorkShiftsInChromosome()) {
            totalEmployeeFitnessPerEmployee.put(employeeWorkShift, 0.0);
            updateEmployeeFitness(employeeRouteSimulatorResults.get(employeeWorkShift));
        }
    }

    public Map<String, EmployeeFitnessValue> getEmployeeFitnessValues() {
        return employeeFitnessValues;
    }

    public EmployeeFitnessValue getEmployeeFitnessValues(String employeeId) {
        return employeeFitnessValues.get(employeeId);
    }

    public Set<String> getEmployeeFitnessValueIds() {
        return employeeFitnessValues.keySet();
    }

    public double getTotalFitness() {
        return totalEmployeeFitness + calculateUnallocatedFitness();
    }

    @Override
    public String toString() {
        return "\tTotal fitness: " + formatDecimal(getTotalFitness());
    }

    private String formatDecimal(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(number);
    }

    private void initializeFitnessDataStructureWithRouteSimulator() {
        for (EmployeeWorkShift employeeWorkShift : individual.getChromosome().getEmployeeWorkShiftsInChromosome()) {
            updateSimulatedRouteFor(routeSimulator.simulateRoute(individual, employeeWorkShift));
        }
    }

    public RouteSimulatorResult getEmployeeRouteSimulatorResultsFor(EmployeeWorkShift employeeWorkShift) {
        return employeeRouteSimulatorResults.get(employeeWorkShift);
    }

    public Collection<RouteSimulatorResult> getEmployeeRouteSimulatorResults() {
        return employeeRouteSimulatorResults.values();
    }

    public void updateSimulatedRouteFor(RouteSimulatorResult routeSimulatorResult) {
        employeeRouteSimulatorResults.put(routeSimulatorResult.getEmployeeWorkShift(), routeSimulatorResult);
    }

    public Individual getIndividual() {
        return individual;
    }

    @Override
    protected void addFieldInfo(Field field, Map<String, Object> fieldMap) throws IllegalAccessException {
        fieldMap.put(field.getName(), field.get(this));
    }
}
