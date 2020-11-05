package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.Objective;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.ObjectivesIntraRouteHandler;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.routeResult.Visit;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Edge;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;

import java.util.PriorityQueue;

/**
 * The labelling algorithm is a resource constrained shortest path algorithm.
 * It finds the minimum cost path from the office through all tasks in the given order.
 */
public class LabellingAlgorithm {

    private SearchGraph graph;
    private PriorityQueue<Label> unExtendedLabels;
    private Label[] labels;
    private Visit[] visits;
    private PriorityQueue<Label> labelsOnDestinationNode;
    private LabelLists labelLists;
    private IExtendInfo nodeExtendInfo;
    private long robustnessTimeSeconds;

    private ObjectivesIntraRouteHandler objectives;
    private ConstraintsIntraRouteHandler constraints;

    private long[] syncedNodesStartTime;
    private long endOfShift;


    public LabellingAlgorithm(SearchGraph graph) {
        this.graph = graph;
        this.unExtendedLabels = new PriorityQueue<>();
        this.labels = new Label[graph.getNodes().size()];
        this.visits = new Visit[graph.getNodes().size()];
        this.labelLists = new LabelLists(graph.getNodes().size(), graph.getNodes().size() * 10);
        this.labelsOnDestinationNode = new PriorityQueue<>();
        this.robustnessTimeSeconds = graph.getRobustTimeSeconds();
        this.objectives = new ObjectivesIntraRouteHandler();
        this.constraints = new ConstraintsIntraRouteHandler();
    }

    public void addObjectiveIntraShift(IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectives.addIntraShiftObjectiveFunction(objectiveIntraShift);
    }

    public void addConstraint(IConstraintIntraRoute constraint) {
        constraints.addConstraint(constraint);
    }

    /**
     * Solves the labelling algorithm and returns the objective value of the route.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param shift                Employee to simulate route for.
     * @return Total fitness value.
     */
    public Double solve(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift shift) {
        return runAlgorithm(nodeExtendInfo, syncedNodesStartTime, shift);
    }

    /**
     * Solves the labelling algorithm and returns the route simulator result.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @return RouteEvaluatorResult or null if route is infeasible.
     */
    public RouteEvaluatorResult solveRouteSimulatorResult(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift employeeWorkShift) {
        Double totalFitness = solve(nodeExtendInfo, syncedNodesStartTime, employeeWorkShift);
        if (totalFitness == null)
            return null;
        return createRouteSimulatorResult(totalFitness, employeeWorkShift, syncedNodesStartTime);
    }

    private RouteEvaluatorResult createRouteSimulatorResult(double totalFitness, IShift employeeWorkShift, long[] syncedNodesStartTime) {

        Label bestLabel = labelsOnDestinationNode.peek();
        if (bestLabel == null)
            return null;
        RouteEvaluatorResult simulatorResult = new RouteEvaluatorResult(employeeWorkShift);
        int visitCnt = extractVisitsAndSyncedStartTime(bestLabel, simulatorResult, syncedNodesStartTime);
        simulatorResult.setObjectiveValue(totalFitness);
        simulatorResult.addVisits(visits, visitCnt);
        simulatorResult.updateTimeOfOfficeReturn(labels[0].getCurrentTime());
        return simulatorResult;
    }

    /**
     * @return null if infeasible otherwise the fitness value
     */
    private Double runAlgorithm(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, IShift employeeWorkShift) {
        long employeeShiftStartTime = employeeWorkShift.getStartTime();
        IResource emptyResource = nodeExtendInfo.createEmptyResource();
        Label currentLabel = createStartLabel(employeeShiftStartTime, emptyResource);
        return runAlgorithmWithStartLabel(nodeExtendInfo, syncedNodesStartTime, employeeWorkShift, currentLabel);
    }

    private Double runAlgorithmWithStartLabel(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime,
                                              IShift employeeWorkShift, Label startLabel) {
        long employeeShiftEndTime = employeeWorkShift.getEndTime();
        this.nodeExtendInfo = nodeExtendInfo;
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.endOfShift = employeeShiftEndTime;
        labelLists.clear();
        solveLabellingAlgorithm(startLabel);
        Label bestLabel = labelsOnDestinationNode.peek();
        return bestLabel == null ? null : bestLabel.getObjective().getObjectiveValue();
    }

    private void solveLabellingAlgorithm(Label currentLabel) {
        unExtendedLabels.clear();
        labelsOnDestinationNode.clear();
        while (currentLabel != null) {
            extendLabelToAllPossibleTasks(currentLabel, labelsOnDestinationNode);
            currentLabel = unExtendedLabels.poll();
            while (!unExtendedLabels.isEmpty() && currentLabel.isClosed())
                currentLabel = unExtendedLabels.poll();
            if (optimalSolutionFound(currentLabel))
                break;
        }
    }

    private Label createStartLabel(long startTime, IResource emptyResource) {
        return new Label(null, graph.getOffice(), graph.getOffice(),
                new Objective(0.0), emptyResource, startTime, 0, startTime);
    }

    private long calcArrivalTimeNextTask(Label thisLabel, boolean requirePhysicalAppearance, long travelTime) {
        long actualTravelTime = travelTime;
        if (requirePhysicalAppearance) {
            actualTravelTime = Math.max(travelTime - (thisLabel.currentTime - thisLabel.canLeaveLocationAtTime), 0);
        }
        return actualTravelTime + thisLabel.currentTime + thisLabel.node.getDurationSeconds() + robustnessTimeSeconds;
    }


    private long updateCanLeaveLocationAt(Label thisLabel, boolean requirePhysicalAppearance, long startOfServiceNextTask) {
        if (requirePhysicalAppearance)
            return startOfServiceNextTask;
        else {
            return thisLabel.canLeaveLocationAtTime + thisLabel.node.getDurationSeconds() + robustnessTimeSeconds;
        }
    }

    private long calcStartOfServiceNextTask(Label thisLabel, Node nextNode, boolean taskRequirePhysicalAppearance, long travelTime) {
        long arrivalTimeNextTask = calcArrivalTimeNextTask(thisLabel, taskRequirePhysicalAppearance, travelTime);
        long earliestStartTimeNextTask = findEarliestStartTimeNextTask(nextNode);
        return Math.max(arrivalTimeNextTask, earliestStartTimeNextTask);
    }

    public boolean isFeasible(long earliestOfficeReturn, ITask task, long startOfServiceNextTask, long syncedLatestStart) {
        ConstraintInfo constraintInfo = new ConstraintInfo(endOfShift, earliestOfficeReturn, task, startOfServiceNextTask, syncedLatestStart);
        return constraints.isFeasible(constraintInfo);
    }

    public Label extendAlong(Label thisLabel, ExtendToInfo extendToInfo) {
        Node nextNode = extendToInfo.getToNode();
        boolean taskRequirePhysicalAppearance = nextNode.getRequirePhysicalAppearance();
        Node newLocation = findNewLocation(thisLabel, taskRequirePhysicalAppearance, nextNode);
        long travelTime = getTravelTime(thisLabel, nextNode, newLocation);
        long startOfServiceNextTask = calcStartOfServiceNextTask(thisLabel, nextNode, taskRequirePhysicalAppearance, travelTime);

        long earliestOfficeReturn = calcEarliestPossibleReturnToOfficeTime(nextNode, newLocation, startOfServiceNextTask);
        long syncedTaskLatestStartTime = nextNode.isSynced() ? syncedNodesStartTime[nextNode.getId()] : -1;
        if (!isFeasible(earliestOfficeReturn, nextNode.getTask(), startOfServiceNextTask, syncedTaskLatestStartTime))
            return null;

        long canLeaveLocationAt = updateCanLeaveLocationAt(thisLabel, taskRequirePhysicalAppearance, startOfServiceNextTask);
        return buildNewLabel(thisLabel, extendToInfo, nextNode, newLocation, travelTime,
                startOfServiceNextTask, canLeaveLocationAt, syncedTaskLatestStartTime);
    }

    private Label buildNewLabel(Label thisLabel, ExtendToInfo extendToInfo, Node nextNode, Node newLocation, long travelTime, long startOfServiceNextTask, long canLeaveLocationAt, long syncedTaskLatestStartTime) {
        Objective objective = thisLabel.objective.extend(null, nextNode, travelTime, startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift, objectives);
        IResource resources = thisLabel.resources.extend(extendToInfo);
        return new Label(thisLabel, nextNode, newLocation, objective, resources, startOfServiceNextTask, travelTime, canLeaveLocationAt);
    }

    private long getTravelTime(Label thisLabel, Node nextNode, Node newLocation) {
        Edge edge = newLocation == thisLabel.currentLocation ? null : getEdgeToNextNode(thisLabel, nextNode);
        if (edge == null) {
            return 0;
        } else
            return edge.getTravelTime();
    }

    private long calcEarliestPossibleReturnToOfficeTime(Node nextNode, Node currentLocation, long startOfServiceNextTask) {
        return startOfServiceNextTask + nextNode.getDurationSeconds() + getTravelTimeToOffice(currentLocation);
    }

    public long getTravelTimeToOffice(Node node) {
        if (node.getAddress() == graph.getOffice().getAddress())
            return 0;
        Edge edge = graph.getEdgesNodeToNode()
                .getEdge(node, graph.getOffice());
        return edge == null ? 0 : edge.getTravelTime();
    }

    private Node findNewLocation(Label thisLabel, boolean requirePhysicalAppearance, Node nextNode) {
        return requirePhysicalAppearance || nextNode.getRequirePhysicalAppearance() ? nextNode : thisLabel.currentLocation;
    }

    private long findEarliestStartTimeNextTask(Node toNode) {
        if (toNode.isSynced()) {
            return syncedNodesStartTime[toNode.getId()];
        } else {
            return toNode.getStartTime();
        }
    }

    private Edge getEdgeToNextNode(Label thisLabel, Node toNode) {
        return graph.getEdgesNodeToNode().getEdge(thisLabel.currentLocation, toNode);
    }

    private boolean optimalSolutionFound(Label currentLabel) {
        return labelsOnDestinationNode.peek() != null && currentLabel != null &&
                labelsOnDestinationNode.peek().getObjective().getObjectiveValue() < currentLabel.getObjective().getObjectiveValue();
    }

    private int extractVisitsAndSyncedStartTime(Label bestLabel, RouteEvaluatorResult result, long[] syncedNodesStartTime) {
        int labelCnt = collectLabels(bestLabel);
        long totalTravelTime = 0;
        int visitCnt = 0;
        for (int i = labelCnt - 1; i > 0; i--) {
            bestLabel = labels[i];
            visitCnt = addVisit(visitCnt, bestLabel);
            totalTravelTime += getLabelTravelTime(bestLabel);
        }
        totalTravelTime += getLabelTravelTime(labels[0]);

        result.updateTotalTravelTime(totalTravelTime);
        return visitCnt;
    }

    private long getLabelTravelTime(Label label) {
        return label.getTravelTime();
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


    private void extendLabelToAllPossibleTasks(Label label, PriorityQueue<Label> labelsOnDestinationNode) {
        boolean returnToDestinationNode = true;
        for (ExtendToInfo extendToInfo : nodeExtendInfo.extend(label)) {
            returnToDestinationNode = false;
            extendNextNode(label, extendToInfo);
        }
        if (returnToDestinationNode) {
            Label newLabel = extendAlong(label, new ExtendToInfo(graph.getOffice(), 0));
            if (newLabel != null)
                labelsOnDestinationNode.add(newLabel);
        }
    }

    private void extendNextNode(Label label, ExtendToInfo extendToInfo) {
        if (extendToInfo != null) {
            extendLabel(label, extendToInfo);
        }
    }

    private void extendLabel(Label label, ExtendToInfo extendToInfo) {
        Label newLabel;
        newLabel = extendAlong(label, extendToInfo);
        if (newLabel != null) {
            if (labelLists.addAndReturnTrueIfAdded(newLabel.getNode(), newLabel))
                unExtendedLabels.add(newLabel);
        }
    }
}




