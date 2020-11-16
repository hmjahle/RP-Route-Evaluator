package com.visma.of.rp.routeevaluator.solver;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.WeightedObjectiveWithValues;
import com.visma.of.rp.routeevaluator.publicInterfaces.*;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private long[] syncedNodesStartTime;

    public RouteEvaluator(long robustTimeSeconds, ITravelTimeMatrix distanceMatrixMatrix, Collection<ITask> tasks, ILocation officePosition) {
        this(robustTimeSeconds, distanceMatrixMatrix, tasks, officePosition, officePosition);
    }

    public RouteEvaluator(long robustTimeSeconds, ITravelTimeMatrix distanceMatrixMatrix, Collection<ITask> tasks, ILocation origin, ILocation destination) {
        this.graph = new SearchGraph(distanceMatrixMatrix, tasks, origin, destination, robustTimeSeconds);
        this.objectiveFunctions = new ObjectiveFunctionsIntraRouteHandler();
        this.constraints = new ConstraintsIntraRouteHandler();
        this.algorithm = new LabellingAlgorithm(graph, objectiveFunctions, constraints);
        this.firstNodeList = new NodeList(graph.getNodes().size());
        this.secondNodeList = new NodeList(graph.getNodes().size());
        this.syncedNodesStartTime = new long[graph.getNodes().size()];
    }


    public Double evaluateRouteObjective(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime, IShift employeeWorkShift) {
        updateFirstTaskList(tasks, syncedTasksStartTime);
        ExtendInfoOneElement nodeExtendInfoOneElement = new ExtendInfoOneElement();
        nodeExtendInfoOneElement.update(firstNodeList);
        Label bestLabel = algorithm.runAlgorithm(new WeightedObjective(), nodeExtendInfoOneElement, syncedNodesStartTime, employeeWorkShift);
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();

    }

    public Double evaluateRouteObjective(List<ITask> tasks, IShift employeeWorkShift) {
        return evaluateRouteObjective(tasks, null, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param syncedTasksStartTime Map of ALL synced tasks and their start times.
     * @param employeeWorkShift    Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasks(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime, IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjective(), tasks, syncedTasksStartTime, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * For routes with no synced tasks.
     *
     * @param tasks             The route to be evaluated, the order of the list is the order of the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByTheOrderOfTasks(List<ITask> tasks, IShift employeeWorkShift) {
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
    public RouteEvaluatorResult evaluateRouteByOrderOfTasksWithObjectiveValues(List<ITask> tasks, IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjectiveWithValues(), tasks, null, employeeWorkShift);
    }

    /**
     * Evaluates the route given by the tasks input, the order of the tasks is the order of the route.
     * Returns an objective that also contains the individual objective values for the different objective
     * functions in the route evaluator.
     *
     * @param tasks                The route to be evaluated, the order of the list is the order of the route.
     * @param syncedTasksStartTime Map of ALL synced tasks and their start times.
     * @param employeeWorkShift    Employee the route applies to.
     * @return A routeEvaluator result for the evaluated route.
     */
    public RouteEvaluatorResult evaluateRouteByOrderOfTasksWithObjectiveValues(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime, IShift employeeWorkShift) {
        return calcRouteEvaluatorResult(new WeightedObjectiveWithValues(), tasks, syncedTasksStartTime, employeeWorkShift);
    }

    private RouteEvaluatorResult calcRouteEvaluatorResult(IObjective objective, List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime, IShift employeeWorkShift) {
        updateFirstTaskList(tasks, syncedTasksStartTime);
        ExtendInfoOneElement nodeExtendInfoOneElement = new ExtendInfoOneElement();
        nodeExtendInfoOneElement.update(firstNodeList);
        return algorithm.solveRouteEvaluatorResult(objective, nodeExtendInfoOneElement, syncedNodesStartTime, employeeWorkShift);
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
     * Adds an objective function to the route evaluator.
     *
     * @param objectiveFunctionId The id, must be unique.
     * @param objectiveWeight     Weight of the objective.
     * @param objectiveIntraShift The objective function to be added.
     */
    public void addObjectiveIntraShift(String objectiveFunctionId, double objectiveWeight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.addIntraShiftObjectiveFunction(objectiveFunctionId, objectiveWeight, objectiveIntraShift);
    }

    /**
     * Adds an objective function to the route evaluator. With weight one and name equal to the class name.
     *
     * @param objectiveIntraShift The objective function to be added.
     */
    public void addObjectiveIntraShift(IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.addIntraShiftObjectiveFunction(objectiveIntraShift.getClass().getSimpleName(), 1.0, objectiveIntraShift);
    }

    /**
     * Adds an constraint to the route evaluator.
     *
     * @param constraint The constraint to be added.
     */
    public void addConstraint(IConstraintIntraRoute constraint) {
        constraints.addConstraint(constraint);
    }

    private void updateFirstTaskList(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime) {
        setFirstNodeList(tasks);
        if (syncedTasksStartTime != null)
            setSyncedNodesStartTime(syncedTasksStartTime);
    }

    private void updateSecondTaskList(Map<ITask, Long> syncedTasksStartTime, List<ITask> taskSetTwo) {
        setSecondNodeList(taskSetTwo);
        setSyncedNodesStartTime(syncedTasksStartTime);
    }

    private void updateSecondTaskList(ITask task, long syncedStartTime) {
        setSecondNodeList(task);
        if (task.isSynced()) {
            setStartTime(task, syncedStartTime);
        }
    }

    private void setFirstNodeList(List<ITask> tasks) {
        firstNodeList.setNodes(graph, tasks);
    }

    private void setSecondNodeList(ITask task) {
        secondNodeList.setNode(graph, task);
    }

    private void setSecondNodeList(List<ITask> syncedTasks) {
        secondNodeList.setNodes(graph, syncedTasks);
        graph.updateNodeType(syncedTasks);
    }

    private void setSyncedNodesStartTime(Map<ITask, Long> syncedTasksStartTime) {
        for (Map.Entry<ITask, Long> taskStartTime : syncedTasksStartTime.entrySet()) {
            setStartTime(taskStartTime.getKey(), taskStartTime.getValue());
        }
    }

    private void setStartTime(ITask task, long startTime) {
        graph.updateNodeType(task);
        Node node = graph.getNode(task);
        syncedNodesStartTime[node.getNodeId()] = startTime;
    }
}
