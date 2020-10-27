package com.visma.of.rp.routeevaluator.solver;

import com.visma.of.rp.routeevaluator.Interfaces.IDistanceMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.IPosition;
import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteSimulatorResult;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.NodeList;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ExtendInfoOneElement;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.LabellingAlgorithm;
import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The route simulator calculates the fitness of a route.
 * When it is used it will only evaluate fitness' and hard constraints,
 * that relates to things that happens within a route. Hence fitness' like Visit history, work balance,
 * heavy tasks, etc. is not evaluated.
 * W.r.t. hard constraints the same assumption applies. Hence constraints like overtime.
 * Max travel distance on bike / walk, avoid overtime is handled within the route simulator.
 * Where constraints like two incompatible tasks on same route and heavy tasks will be ignored.
 * It is therefore assumed that input to the simulator is feasible w.r.t. to these types of constraints.
 */
public class RouteEvaluator {

    private SearchGraph graph;
    private LabellingAlgorithm algorithm;
    private NodeList firstNodeList;
    private NodeList secondNodeList;
    private long[] syncedNodesStartTime;
    private long[] syncedNodesLatestStartTime;

    public SearchGraph getGraph() {
        return graph;
    }

    public RouteEvaluator(long robustTimeSeconds, Map<TransportMode, IDistanceMatrix> distanceMatrixMatrices, Collection<ITask> tasks, IPosition officePosition) {
        this.graph = new SearchGraph(distanceMatrixMatrices, tasks, officePosition, robustTimeSeconds);
        this.algorithm = new LabellingAlgorithm(graph);
        this.firstNodeList = new NodeList(graph.getNodes().size());
        this.secondNodeList = new NodeList(graph.getNodes().size());
        this.syncedNodesStartTime = new long[graph.getNodes().size()];
        this.syncedNodesLatestStartTime = new long[graph.getNodes().size()];
    }


    public Double simulateRouteCost(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime, IShift employeeWorkShift) {
        updateFirstTaskList(tasks, syncedTasksStartTime);
        ExtendInfoOneElement nodeExtendInfoOneElement = new ExtendInfoOneElement();
        nodeExtendInfoOneElement.update(firstNodeList);
        return algorithm.solveFitness(nodeExtendInfoOneElement, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
    }


    /**
     * Simulates the route given by the tasks input, the order of the tasks is the order of the route.
     *
     * @param tasks             The route to be simulated, the order of the list is the order of the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeSimulator result for the simulated route.
     */
    public RouteSimulatorResult simulateRouteByTheOrderOfTasks(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime, IShift employeeWorkShift) {
        updateFirstTaskList(tasks, syncedTasksStartTime);
        ExtendInfoOneElement nodeExtendInfoOneElement = new ExtendInfoOneElement();
        nodeExtendInfoOneElement.update(firstNodeList);
        return algorithm.solveRouteSimulatorResult(nodeExtendInfoOneElement, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
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
        syncedNodesStartTime[node.getId()] = startTime;
        syncedNodesLatestStartTime[node.getId()] = startTime + task.getSyncedWithIntervalDiffSeconds();
    }
}
