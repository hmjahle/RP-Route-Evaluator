package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;
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

    public LabellingAlgorithm(SearchGraph graph) {
        this.graph = graph;
        this.unExtendedLabels = new PriorityQueue<>();
        this.labels = new Label[graph.getNodes().size()];
        this.visits = new Visit[graph.getNodes().size()];
        this.labelLists = new LabelLists(graph.getNodes().size(), graph.getNodes().size() * 10);
        this.labelsOnDestinationNode = new PriorityQueue<>();
        this.robustnessTimeSeconds = graph.getRobustTimeSeconds();
        this.objectiveFunctions = new ObjectiveFunctionsIntraRouteHandler();
        this.constraints = new ConstraintsIntraRouteHandler();
    }

    /**
     * Solves the labelling algorithm and returns the objective value of the route.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @return Total fitness value, null if infeasible.
     */
    public Double runAlgorithm(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift employeeWorkShift) {
        this.labelLists.clear();
        IResource startResource = nodeExtendInfo.createEmptyResource();
        Label startLabel = createStartLabel(employeeWorkShift.getStartTime(), startResource);
        this.nodeExtendInfo = nodeExtendInfo;
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.endOfShift = employeeWorkShift.getEndTime();
        solveLabellingAlgorithm(startLabel);
        Label bestLabel = this.labelsOnDestinationNode.peek();
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();
    }

    /**
     * Solves the labelling algorithm and returns the route simulator result.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @return RouteEvaluatorResult or null if route is infeasible.
     */
    public RouteEvaluatorResult solveRouteEvaluatorResult(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift employeeWorkShift) {
        Double totalFitness = runAlgorithm(nodeExtendInfo, syncedNodesStartTime, employeeWorkShift);
        if (totalFitness == null)
            return null;
        return buildRouteEvaluatorResult(totalFitness, employeeWorkShift);
    }

    /**
     * Adds an objective function to the route evaluator.
     *
     * @param objectiveFunctionId
     * @param objectiveIntraShift The objective function to be added.
     */
    public void addObjectiveFunctionIntraShift(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.addIntraShiftObjectiveFunction(objectiveFunctionId, weight, objectiveIntraShift);
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
     * Extract the solution from the labels and builds the route evaluator results and the visits with the respective information.
     *
     * @param objectiveValue    Weighted objective value of the intra route objectives.
     * @param employeeWorkShift Work shift for the employee for which the route is calculated.
     * @return Results of the route.
     */
    private RouteEvaluatorResult buildRouteEvaluatorResult(double objectiveValue, IShift employeeWorkShift) {
        Label bestLabel = labelsOnDestinationNode.peek();
        RouteEvaluatorResult evaluatorResult = new RouteEvaluatorResult(employeeWorkShift);
        int visitCnt = extractVisitsAndSyncedStartTime(bestLabel, evaluatorResult);
        evaluatorResult.setObjectiveValue(objectiveValue);
        evaluatorResult.addVisits(visits, visitCnt);
        evaluatorResult.updateTimeOfOfficeReturn(labels[0].getCurrentTime());
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

        WeightedObjective objective = thisLabel.getObjective().extend(nextNode, travelTime, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift, objectiveFunctions);

        IResource resources = thisLabel.getResources().extend(extendToInfo);
        
        return new Label(thisLabel, nextNode, newLocation, objective, resources, startOfServiceNextTask, travelTime,
                canLeaveLocationAt);
    }

    private Label findNextLabel() {
        Label currentLabel = unExtendedLabels.poll();
        while (!unExtendedLabels.isEmpty() && currentLabel != null && currentLabel.isClosed())
            currentLabel = unExtendedLabels.poll();
        return currentLabel;
    }

    private Label createStartLabel(long startTime, IResource emptyResource) {
        return new Label(null, graph.getOrigin(), graph.getOrigin().getLocationId(),
                new WeightedObjective(0.0), emptyResource, startTime, 0, startTime);
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

    private int extractVisitsAndSyncedStartTime(Label bestLabel, RouteEvaluatorResult result) {
        int labelCnt = collectLabels(bestLabel);
        long totalTravelTime = 0;
        int visitCnt = 0;
        for (int i = labelCnt - 1; i > 0; i--) {
            bestLabel = labels[i];
            visitCnt = addVisit(visitCnt, bestLabel);
            totalTravelTime += bestLabel.getTravelTime();
        }
        totalTravelTime += labels[0].getTravelTime();
        result.updateTotalTravelTime(totalTravelTime);
        return visitCnt;
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



