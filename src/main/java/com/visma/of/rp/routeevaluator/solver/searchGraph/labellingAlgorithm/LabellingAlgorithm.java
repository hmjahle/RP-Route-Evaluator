package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.routeResult.Visit;

import java.util.PriorityQueue;

/**
 * The labelling algorithm is a resource constrained shortest path algorithm.
 * It finds the minimum cost path from the office through all tasks in the given order.
 */
public class LabellingAlgorithm {

    private SearchGraph graph;
    private ObjectiveFunctionsIntraRouteHandler objectiveFunctions;
    private ConstraintsIntraRouteHandler constraints;
    private PriorityQueue<Label> unExtendedLabels;
    private Label[] labels;
    private Visit[] visits;
    private PriorityQueue<Label> labelsOnDestinationNode;
    private LabelLists labelLists;
    private IExtendInfo nodeExtendInfo;
    private long[] syncedNodesStartTime;
    private long endOfShift;
    private long robustnessTimeSeconds;

    public LabellingAlgorithm(SearchGraph graph, ObjectiveFunctionsIntraRouteHandler objectiveFunctions, ConstraintsIntraRouteHandler constraints) {
        this.graph = graph;
        this.objectiveFunctions = objectiveFunctions;
        this.constraints = constraints;
        this.unExtendedLabels = new PriorityQueue<>();
        this.labels = new Label[graph.getNodes().size()];
        this.visits = new Visit[graph.getNodes().size()];
        this.labelLists = new LabelLists(graph.getNodes().size(), graph.getNodes().size() * 10);
        this.labelsOnDestinationNode = new PriorityQueue<>();
        this.robustnessTimeSeconds = graph.getRobustTimeSeconds();
    }

    /**
     * Solves the labelling algorithm and returns the objective value of the route.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @return Total fitness value, null if infeasible.
     */
    public Label runAlgorithm(IObjective objective, IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift employeeWorkShift) {
        this.labelLists.clear();
        IResource startResource = nodeExtendInfo.createEmptyResource();
        Label startLabel = createStartLabel(objective, employeeWorkShift.getStartTime(), startResource);
        this.nodeExtendInfo = nodeExtendInfo;
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.endOfShift = employeeWorkShift.getEndTime();
        solveLabellingAlgorithm(startLabel);
        return this.labelsOnDestinationNode.peek();
    }

    /**
     * Solves the labelling algorithm and returns the route simulator result.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @return RouteEvaluatorResult or null if route is infeasible.
     */
    public RouteEvaluatorResult solveRouteEvaluatorResult(IObjective initialObjective, IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift employeeWorkShift) {
        Label bestLabel = runAlgorithm(initialObjective, nodeExtendInfo, syncedNodesStartTime, employeeWorkShift);
        if (bestLabel == null)
            return null;
        return buildRouteEvaluatorResult(bestLabel, employeeWorkShift);
    }

    /**
     * Extract the solution from the labels and builds the route evaluator results and the visits with the respective information.
     *
     * @param bestLabel         Label representing the best route for the employee work shift.
     * @param employeeWorkShift Work shift for the employee for which the route is calculated.
     * @return Results of the route.
     */
    private RouteEvaluatorResult buildRouteEvaluatorResult(Label bestLabel, IShift employeeWorkShift) {
        RouteEvaluatorResult evaluatorResult = new RouteEvaluatorResult(employeeWorkShift, bestLabel.getObjective());
        evaluatorResult.updateTimeOfOfficeReturn(bestLabel.getCurrentTime());
        extractVisitsAndSyncedStartTime(bestLabel, evaluatorResult);
        return evaluatorResult;
    }

    private void solveLabellingAlgorithm(Label startLabel) {
        unExtendedLabels.clear();
        labelsOnDestinationNode.clear();
        Label currentLabel = startLabel;
        while (currentLabel != null) {
            extendLabelToAllPossibleTasks(currentLabel, labelsOnDestinationNode);
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
        long travelTime = getTravelTime(thisLabel, nextNode, newLocation);
        long startOfServiceNextTask = calcStartOfServiceNextTask(thisLabel, nextNode, taskRequirePhysicalAppearance, travelTime);
        long earliestOfficeReturn = calcEarliestPossibleReturnToOfficeTime(nextNode, newLocation, startOfServiceNextTask);
        long syncedTaskLatestStartTime = nextNode.isSynced() ? syncedNodesStartTime[nextNode.getNodeId()] : -1;
        if (!isFeasible(earliestOfficeReturn, nextNode.getTask(), startOfServiceNextTask, syncedTaskLatestStartTime))
            return null;
        long canLeaveLocationAt = updateCanLeaveLocationAt(thisLabel, taskRequirePhysicalAppearance, startOfServiceNextTask);
        return buildNewLabel(thisLabel, extendToInfo, nextNode, newLocation, travelTime,
                startOfServiceNextTask, canLeaveLocationAt, syncedTaskLatestStartTime);
    }

    private void extendLabelToAllPossibleTasks(Label label, PriorityQueue<Label> labelsOnDestinationNode) {
        boolean returnToDestinationNode = true;
        for (ExtendToInfo extendToInfo : nodeExtendInfo.extend(label)) {
            returnToDestinationNode = false;
            extendLabel(label, extendToInfo);
        }
        if (returnToDestinationNode) {
            Label newLabel = extendLabelToNextNode(label, new ExtendToInfo(graph.getDestination(), 0));
            if (newLabel != null)
                labelsOnDestinationNode.add(newLabel);
        }
    }

    private void extendLabel(Label label, ExtendToInfo extendToInfo) {
        Label newLabel = extendLabelToNextNode(label, extendToInfo);
        if (newLabel != null) {
            if (labelLists.addAndReturnTrueIfAdded(newLabel.getNode(), newLabel))
                unExtendedLabels.add(newLabel);
        }
    }

    private Label buildNewLabel(Label thisLabel, ExtendToInfo extendToInfo, Node nextNode, int newLocation, long travelTime,
                                long startOfServiceNextTask, long canLeaveLocationAt, long syncedTaskLatestStartTime) {

        IObjective objective = extend(thisLabel.getObjective(), nextNode, travelTime, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);

        IResource resources = thisLabel.getResources().extend(extendToInfo);

        return new Label(thisLabel, nextNode, newLocation, objective, resources, startOfServiceNextTask, travelTime,
                canLeaveLocationAt);
    }

    public IObjective extend(IObjective currentObjective, Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime,
                             long endOfShift) {
        ITask task = toNode.getTask();
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        return objectiveFunctions.calculateObjectiveValue(currentObjective, travelTime, task,
                startOfServiceNextTask, visitEnd, syncedTaskLatestStartTime, endOfShift);
    }


    private Label findNextLabel() {
        Label currentLabel = unExtendedLabels.poll();
        while (!unExtendedLabels.isEmpty() && currentLabel != null && currentLabel.isClosed())
            currentLabel = unExtendedLabels.poll();
        return currentLabel;
    }

    private Label createStartLabel(IObjective objective, long startTime, IResource emptyResource) {
        return new Label(null, graph.getOrigin(), graph.getOrigin().getLocationId(),
                objective, emptyResource, startTime, 0, startTime);
    }

    private long calcArrivalTimeNextTask(Label thisLabel, boolean requirePhysicalAppearance, long travelTime) {
        long actualTravelTime = travelTime;
        if (requirePhysicalAppearance) {
            actualTravelTime = Math.max(travelTime - (thisLabel.getCurrentTime() - thisLabel.getCanLeaveLocationAtTime()), 0);
        }
        return actualTravelTime + thisLabel.getCurrentTime() + thisLabel.getNode().getDurationSeconds() + robustnessTimeSeconds;
    }

    private long updateCanLeaveLocationAt(Label thisLabel, boolean requirePhysicalAppearance, long startOfServiceNextTask) {
        if (requirePhysicalAppearance)
            return startOfServiceNextTask;
        else {
            return thisLabel.getCanLeaveLocationAtTime() + thisLabel.getNode().getDurationSeconds() + robustnessTimeSeconds;
        }
    }

    private long calcStartOfServiceNextTask(Label thisLabel, Node nextNode, boolean taskRequirePhysicalAppearance, long travelTime) {
        long arrivalTimeNextTask = calcArrivalTimeNextTask(thisLabel, taskRequirePhysicalAppearance, travelTime);
        long earliestStartTimeNextTask = findEarliestStartTimeNextTask(nextNode);
        return Math.max(arrivalTimeNextTask, earliestStartTimeNextTask);
    }

    private boolean isFeasible(long earliestOfficeReturn, ITask task, long startOfServiceNextTask, long syncedLatestStart) {
        ConstraintInfo constraintInfo = new ConstraintInfo(endOfShift, earliestOfficeReturn, task, startOfServiceNextTask, syncedLatestStart);
        return constraints.isFeasible(constraintInfo);
    }

    private long getTravelTime(Label thisLabel, Node nextNode, int newLocation) {
        Long travelTime = newLocation == thisLabel.getCurrentLocationId() ? null : graph.getTravelTime(thisLabel.getCurrentLocationId(), nextNode.getLocationId());
        if (travelTime == null) {
            return 0;
        } else
            return travelTime;
    }

    private long calcEarliestPossibleReturnToOfficeTime(Node nextNode, Integer currentLocation, long startOfServiceNextTask) {
        return startOfServiceNextTask + nextNode.getDurationSeconds() + getTravelTimeToDestination(currentLocation);
    }

    private long getTravelTimeToDestination(Integer node) {
        if (node == graph.getDestination().getLocationId())
            return 0;
        Long travelTime = graph.getTravelTime(node, graph.getDestination().getLocationId());
        return travelTime == null ? 0 : travelTime;
    }

    private int findNewLocation(Label thisLabel, boolean requirePhysicalAppearance, Node nextNode) {
        return requirePhysicalAppearance || nextNode.getRequirePhysicalAppearance() ? nextNode.getLocationId() : thisLabel.getCurrentLocationId();
    }

    private long findEarliestStartTimeNextTask(Node toNode) {
        if (toNode.isSynced()) {
            return syncedNodesStartTime[toNode.getNodeId()];
        } else {
            return toNode.getStartTime();
        }
    }

    private boolean optimalSolutionFound(Label currentLabel) {
        return labelsOnDestinationNode.peek() != null && currentLabel != null &&
                labelsOnDestinationNode.peek().getObjective().getObjectiveValue() < currentLabel.getObjective().getObjectiveValue();
    }

    private void extractVisitsAndSyncedStartTime(Label bestLabel, RouteEvaluatorResult evaluatorResult) {
        int labelCnt = collectLabels(bestLabel);
        int visitCnt = 0;
        for (int i = labelCnt - 1; i > 0; i--) {
            bestLabel = labels[i];
            visitCnt = addVisit(visitCnt, bestLabel);
        }
        evaluatorResult.addVisits(visits, visitCnt);
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
        visits[visitCnt++] = new Visit(currentLabel.getNode().getTask(), currentLabel.getCurrentTime(), currentLabel.getCurrentTime() +
                currentLabel.getNode().getTask().getDuration(),
                currentLabel.getTravelTime(),
                robustnessTimeSeconds);
        return visitCnt;
    }

}



