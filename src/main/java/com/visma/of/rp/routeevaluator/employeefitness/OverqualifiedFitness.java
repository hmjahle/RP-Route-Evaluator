package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;

/**
 * Penalize when an employee performs tasks that requires a lower skill level than it has.
 */
public class OverqualifiedFitness extends EmployeeFitnessValue {

    public static final String ID = "overqualifiedFitness";

    public OverqualifiedFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getOverqualifiedFitnessWeight() *
                problemInstance.getOfficeInfo().getOverQualifiedProportionNormalized());
    }

    private OverqualifiedFitness(OverqualifiedFitness copy) {
        super(copy);
    }

    /**
     * Find the penalty for performing tasks below the skill level of the employee.
     *
     * @param routeSimulatorResult
     * @return
     */
    private double findPenalty(RouteSimulatorResult routeSimulatorResult) {
        double penalty = 0;
        EmployeeWorkShift employeeId = routeSimulatorResult.getEmployeeWorkShift();
        for (Task task : routeSimulatorResult.extractEmployeeRoute())
           if(task.getRequiredSkillLevel() != Task.DEFAULT_SKILL_LEVEL){
                 penalty += findTaskPenalty(employeeId, task);
           }
        return penalty;
    }

    private double findTaskPenalty(EmployeeWorkShift employeeWorkShift, Task task) {
        return employeeWorkShift.getSkillLevelId() - task.getRequiredSkillLevel();
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newValue = findPenalty(routeSimulatorResult);
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), newValue);
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newValue = findPenalty(routeSimulatorResult);
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), newValue);
    }

    /**
     * There is no marginal fitness as this is not related to anything within the route.
     *
     * @param marginalFitnessInfo
     * @return 0
     */
    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo) {
        return 0;
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new OverqualifiedFitness(this);
    }
}
