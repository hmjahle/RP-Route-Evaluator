package com.visma.of.rp.routeevaluator.evaluation.cause;

import com.visma.of.rp.routeevaluator.interfaces.*;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class is used to find the cause for infeasibility when evaluating why a route is feasible. Hence,
 * only "intra route constraints" can be evaluated. It evaluates every constraint or objective individually, which means
 * that all other constraints/objectives will be ignored when evaluating these. Hence it can only check one constraint at the time
 * and not the consequence of having two or more constraints active at the same time.
 */
public class InfeasibilityCauseIdentifier<T extends ITask> {

    Map<String, Map<Integer, RouteEvaluator<T>>> routeEvaluatorsConstraints;
    Map<String, Map<Integer, RouteEvaluator<T>>> routeEvaluatorsObjectives;
    Map<Integer, ITravelTimeMatrix> distanceMatrixMatrix;
    Collection<T> tasks;
    ILocation startLocation;
    ILocation endLocation;

    /**
     * The set of tasks MUST include all tasks that will later have to be evaluated. Hence, evaluating a route containing
     * a task that is not present in the constructor will cause unidentified behaviour.
     *
     * @param tasks                The tasks is to be supported in the evaluator
     * @param distanceMatrixMatrix A distance matrix, all travel modes to be evaluated must be present here.
     * @param startLocation        Location of the office if an office is to be used. Must be in the distance matrix.
     */
    public InfeasibilityCauseIdentifier(Collection<T> tasks, Map<Integer, ITravelTimeMatrix> distanceMatrixMatrix,
                                        ILocation startLocation, ILocation endLocation) {
        this.routeEvaluatorsObjectives = new HashMap<>();
        this.routeEvaluatorsConstraints = new HashMap<>();
        this.tasks = tasks;
        this.distanceMatrixMatrix = distanceMatrixMatrix;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    /**
     * Adds an objective function to be evaluated. This can be used to evaluate the objective independently, so one
     * can see the value of just that objective.
     *
     * @param id                Id of the objective function.
     * @param objectiveFunction Objective function to be evaluated if one wants to check the potential objective value,
     *
     */
    public void addInfeasibilityTesterPair(String id, IObjectiveFunctionIntraRoute objectiveFunction) {
        addInfeasibilityTesterPair(id, objectiveFunction, null);
    }

    /**
     * Adds a constraint to be evaluated. Here the feasibility of this constraint can be evaluated
     *
     * @param id                 Id of the constraint.
     * @param constraintFunction Constraint function to check for feasibility.
     */
    public void addInfeasibilityTesterPair(String id, IConstraintIntraRoute constraintFunction) {
        addInfeasibilityTesterPair(id, null, constraintFunction);
    }

    /**
     * Adds an objective and a constraint to be evaluated as a pair.Therefore it is important that they evaluate in the
     * same way. If not the objective value will not necessarily represent the objective of breaking the constraint.
     *
     * @param id                 Id of the constraint.
     * @param objectiveFunction  Objective function to be if one wants to evaluate the potential objective value of the
     *                           paired constraint.
     * @param constraintFunction Constraint function to check for feasibility with. Can be null but then no information
     *                           about the id will be returned when calling the isFeasible function.
     */
    public void addInfeasibilityTesterPair(String id, IObjectiveFunctionIntraRoute objectiveFunction, IConstraintIntraRoute constraintFunction) {
        Map<Integer, RouteEvaluator<T>> routeEvaluatorObjectives = new HashMap<>();
        Map<Integer, RouteEvaluator<T>> routeEvaluatorConstraints = new HashMap<>();
        for (Map.Entry<Integer, ITravelTimeMatrix> kvp : distanceMatrixMatrix.entrySet()) {
            if (objectiveFunction != null) {
                RouteEvaluator<T> reObj = new RouteEvaluator<>(kvp.getValue(), tasks, startLocation, endLocation);
                reObj.addObjectiveIntraShift(objectiveFunction);
                routeEvaluatorObjectives.put(kvp.getKey(), reObj);
            }
            if (constraintFunction != null) {
                RouteEvaluator<T> reCons = new RouteEvaluator<>(kvp.getValue(), tasks, startLocation, endLocation);
                reCons.addConstraint(constraintFunction);
                routeEvaluatorConstraints.put(kvp.getKey(), reCons);
            }
        }
        routeEvaluatorsObjectives.put(id, routeEvaluatorObjectives);
        routeEvaluatorsConstraints.put(id, routeEvaluatorConstraints);

    }

    /**
     * Evaluate whether all constraints added is feasible. A map for each constraint is returned with its id and whether
     * it is feasible.
     *
     * @param tasks                The route to be evaluated.
     * @param syncedTasksStartTime Start times for potential synced tasks in the route. Can be null if it does not
     *                             contain synced tasks.
     * @param employeeWorkShift    Work shift for which the route is to be evaluated for.
     * @return Map with whether route is feasible for the given function id. Return null if there is no constraints
     */
    public Map<String, Boolean> isFeasible(List<T> tasks, Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        Map<String, Boolean> feasibility = new HashMap<>();
        for (Map.Entry<String, Map<Integer, RouteEvaluator<T>>> kvp : routeEvaluatorsConstraints.entrySet()) {
            boolean feasible = kvp.getValue().get(employeeWorkShift.getTransportMode()).evaluateRouteByTheOrderOfTasks(tasks, syncedTasksStartTime, employeeWorkShift) != null;
            feasibility.put(kvp.getKey(), feasible);
        }
        return feasibility.isEmpty() ? null : feasibility;
    }

    /**
     * Evaluate the objective function for each objective and return the objective values in a map with the id and the
     * corresponding objective value. If a route is infeasible for whatever reason it returns null.
     *
     * @param tasks                The route to be evaluated.
     * @param syncedTasksStartTime Start times for potential synced tasks in the route. Can be null if it does not
     *                             contain synced tasks.
     * @param employeeWorkShift    Work shift for which the route is to be evaluated for.
     * @return Map with objective values for the given function id. Return null if there is no objectives to evaluate or
     * the route is infeasible.
     */
    public Map<String, Double> objective(List<T> tasks, Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        Map<String, Double> objectives = new HashMap<>();
        for (Map.Entry<String, Map<Integer, RouteEvaluator<T>>> kvp : routeEvaluatorsObjectives.entrySet()) {
            RouteEvaluatorResult<T> result = kvp.getValue().get(employeeWorkShift.getTransportMode()).evaluateRouteByTheOrderOfTasks(tasks, syncedTasksStartTime, employeeWorkShift);
            if (result == null)
                return null;
            objectives.put(kvp.getKey(), result.getObjectiveValue());
        }
        return objectives.isEmpty() ? null : objectives;
    }


}