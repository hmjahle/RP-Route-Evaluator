package com.visma.of.rp.routeevaluator.solver;

import com.visma.of.rp.routeevaluator.evaluation.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.evaluation.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjectiveWithValues;
import com.visma.of.rp.routeevaluator.interfaces.*;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.algorithm.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The route evaluator calculates the fitness of a route.
 * When it is used it will only evaluate fitness' and hard constraints,
 * that relates to things that happens within a route. Hence fitness' like Visit history, work balance,
 * heavy tasks, etc. is not evaluated.
 * W.r.t. hard constraints the same assumption applies. Hence constraints like overtime.
 * Max travel distance on bike / walk, avoid overtime is handled within the route evaluator.
 * Where constraints like two incompatible tasks on same route and heavy tasks will be ignored.
 * It is therefore assumed that input to the evaluator is feasible w.r.t. to these types of constraints.
 */
public class RouteEvaluator {

    private SearchGraph graph;
    private ObjectiveFunctionsIntraRouteHandler objectiveFunctions;
    private ConstraintsIntraRouteHandler constraints;
    private LabellingAlgorithm algorithm;
    private NodeList firstNodeList;
    private NodeList secondNodeList;
    private int[] syncedNodesStartTime;


    public RouteEvaluator(ITravelTimeMatrix distanceMatrixMatrix, Collection<? extends ITask> tasks) {
        this(distanceMatrixMatrix, tasks, null, null);
    }

    public RouteEvaluator(ITravelTimeMatrix distanceMatrixMatrix, Collection<? extends ITask> tasks, ILocation officePosition) {
        this(distanceMatrixMatrix, tasks, officePosition, officePosition);
    }

    public RouteEvaluator(ITravelTimeMatrix distanceMatrixMatrix, Collection<? extends ITask> tasks,
                          ILocation origin, ILocation destination) {
        this.graph = new SearchGraph(distanceMatrixMatrix, tasks, origin, destination);
        this.objectiveFunctions = new ObjectiveFunctionsIntraRouteHandler();
        this.constraints = new ConstraintsIntraRouteHandler();
        this.algorithm = new LabellingAlgorithm(graph, objectiveFunctions, constraints);
        this.firstNodeList = new NodeList(graph.getNodes().size());
        this.secondNodeList = new NodeList(graph.getNodes().size());
        this.syncedNodesStartTime = new int[graph.getNodes().size()];
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * Only returns objective value, no route details is returned.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     *                             that are not in the route, this will reduce performance
     * @param employeeWorkShift    Employee the route applies to.
     * @return A double value representing the objective value of the route.
     */
    public Double evaluateRouteObjective(List<? extends ITask> tasks, Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        ExtendInfoOneElement nodeExtendInfoOneElement = initializeOneElementEvaluator(tasks, syncedTasksStartTime);
        Label bestLabel = algorithm.
                runAlgorithm(new WeightedObjective(), nodeExtendInfoOneElement, syncedNodesStartTime, employeeWorkShift);
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();

    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * Only returns objective value, no route details is returned.
     * For routes with no synced tasks.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A double value representing the objective value of the route.
     */
    public Double evaluateRouteObjective(List<? extends ITask> tasks, IShift employeeWorkShift) {
        return evaluateRouteObjective(tasks, null, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * At the same time it finds the optimal position in the route to insert the new task.
     * Only returns objective value, no route details is returned.
     * For routes with no synced tasks.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param insertTask        The task to be inserted into the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A double value representing the objective value of the route.
     */
    public Double evaluateRouteByTheOrderOfTasksInsertTaskObjective(List<? extends ITask> tasks, ITask insertTask,
                                                                    IShift employeeWorkShift) {
        return calcObjectiveInsertTask(tasks, insertTask, null, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * At the same time it finds the optimal position in the route to insert the new task.
     * Only returns objective value, no route details is returned.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param insertTask           The task to be inserted into the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     * @param employeeWorkShift    Employee the route applies to.
     * @return A double value representing the objective value of the route.
     */
    public Double evaluateRouteByTheOrderOfTasksInsertTaskObjective(List<? extends ITask> tasks, ITask insertTask,
                                                                    Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        return calcObjectiveInsertTask(tasks, insertTask, syncedTasksStartTime, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * It evaluates the route where the task at the indices to be skipped is ignored, i.e., removed from the route.
     * Only returns objective value, no route details is returned.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param skipTasksAtIndices   The indices where the tasks to be removed are placed in the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     * @param employeeWorkShift    Employee the route applies to.
     * @return A double value representing the objective value of the route.
     */
    public Double evaluateRouteByTheOrderOfTasksRemoveTaskObjective(List<? extends ITask> tasks, List<Integer> skipTasksAtIndices,
                                                                    Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        return calcObjectiveRemoveTask(tasks, skipTasksAtIndices, syncedTasksStartTime, employeeWorkShift);
    }


    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * It evaluates the route where the task at the index to be skipped is ignored.
     * Only returns objective value, no route details is returned.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param skipTaskAtIndex      The the index where the task to be removed is placed in the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     * @param employeeWorkShift    Employee the route applies to.
     * @return A double value representing the objective value of the route.
     */
    public Double evaluateRouteByTheOrderOfTasksRemoveTaskObjective(List<? extends ITask> tasks, int skipTaskAtIndex,
                                                                    Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        return calcObjectiveRemoveTask(tasks, skipTaskAtIndex, syncedTasksStartTime, employeeWorkShift);
    }


    /**
     * Used to initialize the route evaluator when
     */
    private ExtendInfoOneElement initializeOneElementEvaluator(List<? extends ITask> tasks, Map<ITask, Integer> syncedTasksStartTime) {
        setSyncedNodesStartTime(syncedTasksStartTime);
        updateFirstNodeList(tasks);
        return new ExtendInfoOneElement(firstNodeList);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     *                             that are not in the route, this will reduce performance
     * @param employeeWorkShift    Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasks(List<? extends ITask> tasks, Map<ITask, Integer> syncedTasksStartTime,
                                                               IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, syncedTasksStartTime, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * The route given is split in two based on the criteria, the two lists of tasks are then merged into
     * a new route. This is performed such that each task is inserted in the optimal position in the route.
     *
     * @param tasks                The route to be evaluated.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     *                             that are not in the route, this will reduce performance
     * @param employeeWorkShift    Employee the route applies to.
     * @param criteriaFunction     The function that determines if a tasks should be re-inserted.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(List<? extends ITask> tasks, Map<ITask, Integer> syncedTasksStartTime,
                                                                                      IShift employeeWorkShift, Predicate<ITask> criteriaFunction) {
        List<ITask> fitsCriteria = tasks.stream().filter(criteriaFunction).collect(Collectors.toList());
        List<ITask> doesNotFitCriteria = new ArrayList<>(tasks);
        doesNotFitCriteria.removeAll(fitsCriteria);
        return calcRouteEvaluatorResult(new WeightedObjective(), doesNotFitCriteria, fitsCriteria, syncedTasksStartTime, employeeWorkShift);
    }


    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * For routes with no synced tasks.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasks(List<? extends ITask> tasks, IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, null, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * Returns an objective that also contains the individual objective values for the different objective
     * functions in the route evaluator.
     * For routes with no synced tasks.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByOrderOfTasksWithObjectiveValues(List<? extends ITask> tasks, IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjectiveWithValues(), tasks, null, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * Returns an objective that also contains the individual objective values for the different objective
     * functions in the route evaluator.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     *                             that are not in the route, this will reduce performance
     * @param employeeWorkShift    Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByOrderOfTasksWithObjectiveValues(List<? extends ITask> tasks,
                                                                               Map<ITask, Integer> syncedTasksStartTime,
                                                                               IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjectiveWithValues(), tasks, syncedTasksStartTime, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * At the same time it finds the optimal position in the route to insert the new task.
     * For routes with no synced tasks, the new task to be inserted cannot be synced either.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param insertTask           The task to be inserted into the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     *                             that are not in the route, this will reduce performance
     * @param employeeWorkShift    Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasksInsertTask(List<? extends ITask> tasks, ITask insertTask,
                                                                         Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, insertTask, syncedTasksStartTime, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * At the same time it finds the optimal position in the route to insert the new task.
     * For routes with no synced tasks, the new task to be inserted cannot be synced either.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param insertTask        The task to be inserted into the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasksInsertTask(List<? extends ITask> tasks, ITask insertTask,
                                                                         IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, insertTask, null, employeeWorkShift);
    }

    /**
     * Used to calculate objective of routes when inserting one new task
     */
    private Double calcObjectiveInsertTask(List<? extends ITask> tasks, ITask insertTask,
                                           Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        setSyncedNodesStartTime(syncedTasksStartTime);
        updateFirstNodeList(tasks);
        updateSecondNodeList(insertTask);
        ExtendInfoTwoElements nodeExtendInfoTwoElements = new ExtendInfoTwoElements(firstNodeList, secondNodeList);
        Label bestLabel = algorithm.
                runAlgorithm(new WeightedObjective(), nodeExtendInfoTwoElements, syncedNodesStartTime, employeeWorkShift);
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();
    }

    /**
     * Used to calculate objective of routes when removing one task
     */
    private Double calcObjectiveRemoveTask(List<? extends ITask> tasks, int skipTaskAtIndex,
                                           Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        setSyncedNodesStartTime(syncedTasksStartTime);
        updateFirstNodeList(tasks, skipTaskAtIndex);
        ExtendInfoOneElement nodeExtendInfoOneElement = new ExtendInfoOneElement(firstNodeList);
        Label bestLabel = algorithm.
                runAlgorithm(new WeightedObjective(), nodeExtendInfoOneElement, syncedNodesStartTime, employeeWorkShift);
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();
    }

    /**
     * Used to calculate objective of routes when removing multiple tasks
     */
    private Double calcObjectiveRemoveTask(List<? extends ITask> tasks, List<Integer> skipTasksAtIndices,
                                           Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        setSyncedNodesStartTime(syncedTasksStartTime);
        updateFirstNodeList(tasks, skipTasksAtIndices);
        ExtendInfoOneElement nodeExtendInfoOneElement = new ExtendInfoOneElement(firstNodeList);
        Label bestLabel = algorithm.
                runAlgorithm(new WeightedObjective(), nodeExtendInfoOneElement, syncedNodesStartTime, employeeWorkShift);
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * At the same time it finds the optimal position in the route to insert the new tasks provided.
     * For routes with no synced tasks, the new task to be inserted cannot be synced either.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param insertTasks          The list of tasks to be inserted into the route.
     * @param syncedTasksStartTime Map of ALL synced tasks in the route and their start times. Should not contain tasks
     *                             that are not in the route, this will reduce performance
     * @param employeeWorkShift    Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasksInsertTasks(List<? extends ITask> tasks, List<? extends ITask> insertTasks,
                                                                          Map<ITask, Integer> syncedTasksStartTime,
                                                                          IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, insertTasks, syncedTasksStartTime, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * At the same time it finds the optimal position in the route to insert the new tasks provided.
     * For routes with no synced tasks, the new task to be inserted cannot be synced either.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param insertTasks       The list of tasks to be inserted into the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasksInsertTasks(List<? extends ITask> tasks, List<? extends ITask> insertTasks,
                                                                          IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, insertTasks, null, employeeWorkShift);
    }

    /**
     * Updates the start location used to evaluate routes. The location must be present
     * in the route evaluator, i.e., the travel times matrix given when the route evaluator was constructed.
     *
     * @param originLocation The the location where the route should start.
     */
    public void updateOrigin(ILocation originLocation) {
        graph.updateOrigin(originLocation);
    }

    /**
     * Updates the end location used when evaluating routes. The location must be present in the route evaluator, i.e.,
     * the travel times matrix given when the route evaluator was constructed.
     *
     * @param destinationLocation The the location where the route should end.
     */
    public void updateDestination(ILocation destinationLocation) {
        graph.updateDestination(destinationLocation);
    }

    /**
     * Open ended routes ensures that the route ends at the last task in the route. Hence the route cannot have a
     * destination.
     * The destination of a route is overwritten when this is set. In the same way when the destination is updated the
     * route is no longer considered to be open ended.
     */
    public void useOpenEndedRoutes() {
        graph.useOpenEndedRoutes();
    }


    public void useOpenStartRoutes() {
        graph.useStartRoutes();
    }

    /**
     * Adds an objective function to the route evaluator.
     *
     * @param objectiveFunctionId The id, must be unique.
     * @param objectiveWeight     Weight of the objective.
     * @param objectiveIntraShift The objective function to be added.
     */
    public void addObjectiveIntraShift(String objectiveFunctionId, double objectiveWeight,
                                       IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.addIntraShiftObjectiveFunction(objectiveFunctionId, objectiveWeight, objectiveIntraShift);
    }

    /**
     * Adds an objective function to the route evaluator. With weight one and name equal to the class name.
     *
     * @param objectiveIntraShift The objective function to be added.
     */
    public void addObjectiveIntraShift(IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.addIntraShiftObjectiveFunction(
                objectiveIntraShift.getClass().getSimpleName(), 1.0, objectiveIntraShift);
    }

    /**
     * Adds an constraint to the route evaluator.
     *
     * @param constraint The constraint to be added.
     */
    public void addConstraint(IConstraintIntraRoute constraint) {
        constraints.addConstraint(constraint);
    }

    /**
     * Used to calculate routes without inserting new tasks.
     */
    private RouteEvaluatorResult calcRouteEvaluatorResult(IObjective objective, List<? extends ITask> tasks,
                                                          Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        ExtendInfoOneElement nodeExtendInfoOneElement = initializeOneElementEvaluator(tasks, syncedTasksStartTime);
        return algorithm.solveRouteEvaluatorResult(objective, nodeExtendInfoOneElement, syncedNodesStartTime, employeeWorkShift);
    }

    /**
     * Used to calculate routes when inserting one new task
     */
    private RouteEvaluatorResult calcRouteEvaluatorResult(IObjective objective, List<? extends ITask> tasks, ITask insertTask,
                                                          Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        setSyncedNodesStartTime(syncedTasksStartTime);
        updateFirstNodeList(tasks);
        updateSecondNodeList(insertTask);
        ExtendInfoTwoElements nodeExtendInfoTwoElements = new ExtendInfoTwoElements(firstNodeList, secondNodeList);
        return algorithm.solveRouteEvaluatorResult(objective, nodeExtendInfoTwoElements, syncedNodesStartTime, employeeWorkShift);
    }

    /**
     * Used to calculate routes when inserting multiple new tasks.
     */
    private RouteEvaluatorResult calcRouteEvaluatorResult(IObjective objective, List<? extends ITask> tasks, List<? extends ITask> insertTasks,
                                                          Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        setSyncedNodesStartTime(syncedTasksStartTime);
        updateFirstNodeList(tasks);
        updateSecondNodeList(insertTasks);
        ExtendInfoTwoElements nodeExtendInfoTwoElements = new ExtendInfoTwoElements(firstNodeList, secondNodeList);
        return algorithm.solveRouteEvaluatorResult(objective, nodeExtendInfoTwoElements, syncedNodesStartTime, employeeWorkShift);
    }

    private void updateFirstNodeList(List<? extends ITask> tasks) {
        firstNodeList.initializeWithNodes(graph, tasks);
    }

    private void updateFirstNodeList(List<? extends ITask> tasks, int skipTaskAtIndex) {
        firstNodeList.initializeWithNodes(graph, tasks, skipTaskAtIndex);
    }

    private void updateFirstNodeList(List<? extends ITask> tasks, List<Integer> skipTasksAtIndices) {
        firstNodeList.initializeWithNodes(graph, tasks, skipTasksAtIndices);
    }

    private void updateSecondNodeList(ITask task) {
        secondNodeList.initializeWithNode(graph, task);
    }

    private void updateSecondNodeList(List<? extends ITask> tasks) {
        secondNodeList.initializeWithNodes(graph, tasks);
    }

    private void setSyncedNodesStartTime(Map<ITask, Integer> syncedTasksStartTime) {
        if (syncedTasksStartTime != null)
            for (Map.Entry<ITask, Integer> taskStartTime : syncedTasksStartTime.entrySet()) {
                setStartTime(taskStartTime.getKey(), taskStartTime.getValue());
            }
    }

    private void setStartTime(ITask task, int startTime) {
        Node node = graph.getNode(task);
        syncedNodesStartTime[node.getNodeId()] = startTime;
    }
}
