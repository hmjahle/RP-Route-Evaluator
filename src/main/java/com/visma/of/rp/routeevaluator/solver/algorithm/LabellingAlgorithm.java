package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.evaluation.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.evaluation.info.ConstraintInfo;
import com.visma.of.rp.routeevaluator.evaluation.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.Route;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.results.Visit;

import java.util.Enumeration;

/**
 * The labelling algorithm is a resource constrained shortest path algorithm.
 * It finds the minimum cost path from the office through all tasks in the given order.
 */
public class LabellingAlgorithm {

    private SearchGraph graph;
    private ObjectiveFunctionsIntraRouteHandler objectiveFunctions;
    private ConstraintsIntraRouteHandler constraints;
    private LabelQueue unExtendedLabels;
    private Label[] labels;
    private Visit[] visits;
    private Label bestLabelOnDestination;
    private LabelLists labelLists;
    private IExtendInfo nodeExtendInfo;
    private int[] syncedNodesStartTime;
    private int endOfShift;

    public LabellingAlgorithm(SearchGraph graph, ObjectiveFunctionsIntraRouteHandler objectiveFunctions, ConstraintsIntraRouteHandler constraints) {
        this.graph = graph;
        this.objectiveFunctions = objectiveFunctions;
        this.constraints = constraints;
        this.labels = new Label[graph.getNodes().size()];
        this.visits = new Visit[graph.getNodes().size()];
        this.labelLists = new LabelLists(graph.getNodes().size(), graph.getNodes().size() * 10);
        this.unExtendedLabels = new LabelQueue();
        this.bestLabelOnDestination = null;

    }

    /**
     * Solves the labelling algorithm and returns the objective value of the route.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @param objective            Starting objective.
     * @return Total fitness value, null if infeasible.
     */
    public Label runAlgorithm(IObjective objective, IExtendInfo nodeExtendInfo, int[] syncedNodesStartTime, IShift employeeWorkShift) {
        this.labelLists.clear();
        IResource startResource = nodeExtendInfo.createEmptyResource();
        Label startLabel = createStartLabel(objective, employeeWorkShift.getStartTime(), startResource);
        this.nodeExtendInfo = nodeExtendInfo;
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.endOfShift = employeeWorkShift.getEndTime();
        solveLabellingAlgorithm(startLabel);
        return this.bestLabelOnDestination;
    }

    /**
     * Solves the labelling algorithm and returns the route simulator result.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @param initialObjective     Starting objective.
     * @return RouteEvaluatorResult or null if route is infeasible.
     */
    public RouteEvaluatorResult solveRouteEvaluatorResult(IObjective initialObjective, IExtendInfo nodeExtendInfo, int[] syncedNodesStartTime, IShift employeeWorkShift) {
        Label bestLabel = runAlgorithm(initialObjective, nodeExtendInfo, syncedNodesStartTime, employeeWorkShift);
        if (bestLabel == null)
            return null;
        return buildRouteEvaluatorResult(bestLabel);
    }

    /**
     * Extract the solution from the labels and builds the route evaluator results and the visits with the respective information.
     *
     * @param bestLabel Label representing the best route for the employee work shift.
     * @return Results of the route.
     */
    private RouteEvaluatorResult buildRouteEvaluatorResult(Label bestLabel) {
        Route route = new Route();
        route.setTimeOfArrivalAtDestination(bestLabel.getCurrentTime());
        extractVisitsAndSyncedStartTime(bestLabel, route);
        return new RouteEvaluatorResult(bestLabel.getObjective(), route);
    }

    private void solveLabellingAlgorithm(Label startLabel) {
        unExtendedLabels.clear();
        bestLabelOnDestination = null;
        Label currentLabel = startLabel;
        while (currentLabel != null) {
            extendLabelToAllPossibleTasks(currentLabel);
            currentLabel = findNextLabel();
            if (optimalSolutionFound(currentLabel))
                break;
        }
    }

    /**
     * Extends a label to the next node and returns the label to be put on that node.
     *
     * @param thisLabel    The label to be extended.
     * @param extendToInfo The info the contains the node to be extended.
     * @return The label to be placed on the next node.
     */
    public Label extendLabelToNextNode(Label thisLabel, ExtendToInfo extendToInfo) {
        Node nextNode = extendToInfo.getToNode();
        boolean taskRequirePhysicalAppearance = nextNode.getRequirePhysicalAppearance();
        int newLocation = findNewLocation(thisLabel, taskRequirePhysicalAppearance, nextNode);
        int travelTime = getTravelTime(thisLabel, nextNode, newLocation);
        boolean nextNodeIsSynced = nextNode.isSynced();
        int startOfServiceNextTask = calcStartOfServiceNextTask(thisLabel, nextNode, taskRequirePhysicalAppearance, travelTime, nextNodeIsSynced);
        IObjective objective = evaluateFeasibilityAndObjective(thisLabel, nextNode, startOfServiceNextTask, travelTime, nextNodeIsSynced, newLocation);
        if (objective == null)
            return null;
        IResource resources = thisLabel.getResources().extend(extendToInfo);
        if (taskRequirePhysicalAppearance)
            return new Label(thisLabel, nextNode, newLocation, objective, resources, startOfServiceNextTask, travelTime);
        else {
            int canLeaveLocationAt = updateCanLeaveLocationAt(thisLabel);
            Label newLabel = new Label(thisLabel, nextNode, newLocation, objective, resources, startOfServiceNextTask, travelTime);
            newLabel.setCanLeaveLocationAtTime(canLeaveLocationAt);
            return newLabel;
        }
    }

    private void extendLabelToAllPossibleTasks(Label label) {
        boolean returnToDestinationNode = true;
        Enumeration<ExtendToInfo> enumerator = nodeExtendInfo.extend(label);
        while (enumerator.hasMoreElements()) {
            returnToDestinationNode = false;
            extendLabel(label, enumerator.nextElement());
        }
        if (returnToDestinationNode) {
            Label newLabel = extendLabelToNextNode(label, new ExtendToInfo(graph.getDestination(), 0));
            if (newLabel != null && (bestLabelOnDestination == null || newLabel.compareTo(bestLabelOnDestination) < 0))
                bestLabelOnDestination = newLabel;
        }
    }

    private void extendLabel(Label label, ExtendToInfo extendToInfo) {
        Label newLabel = extendLabelToNextNode(label, extendToInfo);
        if (newLabel != null && labelLists.addLabelOnNode(newLabel.getNode(), newLabel))
            unExtendedLabels.addLabel(newLabel);
    }

    public IObjective extend(IObjective currentObjective, Node toNode, int travelTime, int startOfServiceNextTask, int syncedTaskLatestStartTime,
                             int endOfShift) {
        ITask task = toNode.getTask();
        int visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        return objectiveFunctions.calculateObjectiveValue(currentObjective, travelTime, task,
                startOfServiceNextTask, visitEnd, syncedTaskLatestStartTime, endOfShift);
    }

    private Label findNextLabel() {
        Label currentLabel = unExtendedLabels.poll();
        while (currentLabel != null && currentLabel.isClosed())
            currentLabel = unExtendedLabels.poll();
        return currentLabel;
    }

    private Label createStartLabel(IObjective objective, int startTime, IResource emptyResource) {
        return new Label(null, graph.getOrigin(), graph.getOrigin().getLocationId(),
                objective, emptyResource, startTime, 0);
    }

    private int calcArrivalTimeNextTask(Label thisLabel, boolean requirePhysicalAppearance, int travelTime) {
        int actualTravelTime = travelTime;
        if (requirePhysicalAppearance) {
            actualTravelTime = Math.max(travelTime - (thisLabel.getCurrentTime() - thisLabel.getCanLeaveLocationAtTime()), 0);
        }
        return actualTravelTime + thisLabel.getCurrentTime() + thisLabel.getNode().getDurationSeconds();
    }

    private int updateCanLeaveLocationAt(Label thisLabel) {
        return thisLabel.getCanLeaveLocationAtTime() + thisLabel.getNode().getDurationSeconds();
    }

    private int calcStartOfServiceNextTask(Label thisLabel, Node nextNode, boolean taskRequirePhysicalAppearance, int travelTime, boolean nextNodeIsSynced) {
        int arrivalTimeNextTask = calcArrivalTimeNextTask(thisLabel, taskRequirePhysicalAppearance, travelTime);
        int earliestStartTimeNextTask = findEarliestStartTimeNextTask(nextNode, nextNodeIsSynced);
        return Math.max(arrivalTimeNextTask, earliestStartTimeNextTask);
    }

    private IObjective evaluateFeasibilityAndObjective(Label thisLabel, Node nextNode, int startOfServiceNextTask,
                                                       int travelTime, boolean nextNodeIsSynced, int newLocation) {
        int syncedTaskLatestStartTime = nextNodeIsSynced ? syncedNodesStartTime[nextNode.getNodeId()] : -1;
        int earliestOfficeReturn = calcEarliestPossibleReturnToOfficeTime(nextNode, newLocation, startOfServiceNextTask);
        ConstraintInfo constraintInfo = new ConstraintInfo(endOfShift, earliestOfficeReturn, nextNode.getTask(), startOfServiceNextTask, syncedTaskLatestStartTime);
        if (!constraints.isFeasible(constraintInfo))
            return null;
        return extend(thisLabel.getObjective(), nextNode, travelTime, startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift);
    }

    private int getTravelTime(Label thisLabel, Node nextNode, int newLocation) {
        Integer travelTime = newLocation == thisLabel.getCurrentLocationId() ? null : graph.getTravelTime(thisLabel.getCurrentLocationId(), nextNode.getLocationId());
        if (travelTime == null) {
            return 0;
        } else
            return travelTime;
    }

    private int calcEarliestPossibleReturnToOfficeTime(Node nextNode, Integer currentLocation, int startOfServiceNextTask) {
        return startOfServiceNextTask + nextNode.getDurationSeconds() + getTravelTimeToDestination(currentLocation);
    }

    private int getTravelTimeToDestination(Integer node) {
        if (node == graph.getDestination().getLocationId())
            return 0;
        Integer travelTime = graph.getTravelTime(node, graph.getDestination().getLocationId());
        return travelTime == null ? 0 : travelTime;
    }

    private int findNewLocation(Label thisLabel, boolean requirePhysicalAppearance, Node nextNode) {
        return requirePhysicalAppearance ? nextNode.getLocationId() : thisLabel.getCurrentLocationId();
    }

    private int findEarliestStartTimeNextTask(Node nextNode, boolean nextNodeIsSynced) {
        if (nextNodeIsSynced) {
            return syncedNodesStartTime[nextNode.getNodeId()];
        } else {
            return nextNode.getStartTime();
        }
    }

    private boolean optimalSolutionFound(Label currentLabel) {
        return bestLabelOnDestination != null && currentLabel != null &&
                bestLabelOnDestination.getObjective().getObjectiveValue() < currentLabel.getObjective().getObjectiveValue();
    }

    private void extractVisitsAndSyncedStartTime(Label bestLabel, Route route) {
        int labelCnt = collectLabels(bestLabel);
        int visitCnt = 0;
        for (int i = labelCnt - 1; i > 0; i--) {
            bestLabel = labels[i];
            visitCnt = addVisit(visitCnt, bestLabel);
        }
        route.addVisits(visits, visitCnt);
    }

    private int collectLabels(Label currentLabel) {
        int labelCnt = 0;
        while (currentLabel.getPrevious() != null) {
            labels[labelCnt++] = currentLabel;
            currentLabel = currentLabel.getPrevious();
        }
        return labelCnt;
    }

    private int addVisit(int visitCnt, Label currentLabel) {
        visits[visitCnt++] = new Visit(currentLabel.getNode().getTask(), currentLabel.getCurrentTime(),
                currentLabel.getTravelTime());
        return visitCnt;
    }

}
