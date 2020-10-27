package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.objectives.Objective;
import com.visma.of.rp.routeevaluator.hardConstraints.HardConstraintsIncremental;
import com.visma.of.rp.routeevaluator.routeResult.RouteSimulatorResult;
import com.visma.of.rp.routeevaluator.routeResult.Visit;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;
import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;
import com.visma.of.rp.routeevaluator.transportInfo.TravelInfo;

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
    private SearchInfo searchInfo;
    private IExtendInfo nodeExtendInfo;
    private long robustnessTimeSeconds;

    public LabellingAlgorithm(SearchGraph graph) {
        this.graph = graph;
        this.unExtendedLabels = new PriorityQueue<>();
        this.labels = new Label[graph.getNodes().size()];
        this.visits = new Visit[graph.getNodes().size()];
        this.labelLists = new LabelLists(graph.getNodes().size(), graph.getNodes().size() * 10);
        this.searchInfo = new SearchInfo(this.graph);
        this.labelsOnDestinationNode = new PriorityQueue<>();
        this.robustnessTimeSeconds = graph.getRobustTimeSeconds();
    }


    protected void replaceHardConstraintsEvaluator(HardConstraintsIncremental hardConstraintsEvaluator) {
        this.searchInfo.replaceHardConstraintsEvaluator(hardConstraintsEvaluator);
    }

    /**
     * Solves the labelling algorithm and returns the objective value of the route.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param shift    Employee to simulate route for.
     * @return Total fitness value.
     */
    public Double solve(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, long[] syncedNodesLatestStartTime, IShift shift) {
        return runAlgorithm(nodeExtendInfo, syncedNodesStartTime, syncedNodesLatestStartTime, shift);
    }

    /**
     * Solves the labelling algorithm and returns the route simulator result.
     *
     * @param nodeExtendInfo       Information on how to extend labels and which resources to use.
     * @param syncedNodesStartTime Intended start time of synced tasks.
     * @param employeeWorkShift    Employee to simulate route for.
     * @return RouteSimulatorResult or null if route is infeasible.
     */
    public RouteSimulatorResult solveRouteSimulatorResult(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime,
                                                          long[] syncedNodesLatestStartTime,
                                                          IShift employeeWorkShift) {
        Double totalFitness = solve(nodeExtendInfo, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
        if (totalFitness == null)
            return null;
        return createRouteSimulatorResult(totalFitness, employeeWorkShift, syncedNodesStartTime);
    }

    private RouteSimulatorResult createRouteSimulatorResult(double totalFitness, IShift employeeWorkShift, long[] syncedNodesStartTime) {

        Label bestLabel = labelsOnDestinationNode.peek();
        if (bestLabel == null)
            return null;
        TravelInfo fullRouteTravelInfo = new TravelInfo(0, 0);
        RouteSimulatorResult simulatorResult = new RouteSimulatorResult(employeeWorkShift);
        int visitCnt = extractVisitsAndSyncedStartTime(fullRouteTravelInfo, bestLabel, simulatorResult, syncedNodesStartTime);
        simulatorResult.setObjectiveValue(totalFitness);
        simulatorResult.addVisits(visits, visitCnt, fullRouteTravelInfo);
        simulatorResult.updateTimeOfOfficeReturn(labels[0].getCurrentTime());
        return simulatorResult;
    }

    /**
     * @return null if infeasible otherwise the fitness value
     */
    private Double runAlgorithm(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime, long[] syncedNodesLatestStartTime, IShift employeeWorkShift) {
        long employeeShiftStartTime = employeeWorkShift.getStartTime();
        IResource emptyResource = nodeExtendInfo.createEmptyResource();
        Label currentLabel = createStartLabel(employeeShiftStartTime, emptyResource);
        return runAlgorithmWithStartLabel(nodeExtendInfo, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift, currentLabel);
    }

    private Double runAlgorithmWithStartLabel(IExtendInfo nodeExtendInfo, long[] syncedNodesStartTime,
                                              long[] syncedNodesLatestStartTime, IShift employeeWorkShift, Label startLabel) {
        TransportMode employeeTransportMode = employeeWorkShift.getTransport();
        long employeeShiftEndTime = employeeWorkShift.getEndTime();
        this.nodeExtendInfo = nodeExtendInfo;
        graph.setEdgesTransportMode(employeeTransportMode);
        searchInfo.update(syncedNodesStartTime, syncedNodesLatestStartTime, employeeShiftEndTime, employeeWorkShift);
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
        return new Label(searchInfo, null, graph.getOffice(), graph.getOffice(), null,
                new Objective(0.0), startTime, 0, emptyResource, graph.getRobustTimeSeconds());
    }

    private boolean optimalSolutionFound(Label currentLabel) {
        return labelsOnDestinationNode.peek() != null && currentLabel != null &&
                labelsOnDestinationNode.peek().getObjective().getObjectiveValue() < currentLabel.getObjective().getObjectiveValue();
    }

    private int extractVisitsAndSyncedStartTime(TravelInfo travelInfo, Label bestLabel, RouteSimulatorResult result, long[] syncedNodesStartTime) {
        int labelCnt = collectLabels(bestLabel);
        long totalTravelTimeWithParking = 0;
        long totalTravelTimeWithoutParking = 0;
        int visitCnt = 0;
        for (int i = labelCnt - 1; i > 0; i--) {
            bestLabel = labels[i];
            visitCnt = addVisit(visitCnt, bestLabel);
            if (bestLabel.getNode().isSynced())
                result.addChromosomeStartTime(bestLabel.getNode().getTask(), syncedNodesStartTime[bestLabel.getNode().getId()]);
            if (bestLabel.getEdge() != null) {
                totalTravelTimeWithParking += bestLabel.getEdge().getTravelInfo().getTravelTimeWithParking();
                totalTravelTimeWithoutParking += bestLabel.getEdge().getTravelInfo().getRawTravelTime();
            }
        }

        addTravelInfo(travelInfo, totalTravelTimeWithParking, totalTravelTimeWithoutParking);
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
                currentLabel.getNode().getTask().getDurationSeconds(),
                currentLabel.getEdge() != null ? currentLabel.getEdge().getTravelInfo().getTravelTimeWithParking() : 0,
                robustnessTimeSeconds);
        return visitCnt;
    }

    private void addTravelInfo(TravelInfo travelInfo, long travelTimeWithParking, long travelTimeWithoutParking) {
        if (labels[0].getEdge() != null) {
            travelTimeWithParking += labels[0].getEdge().getTravelInfo().getTravelTimeWithParking();
            travelTimeWithoutParking += labels[0].getEdge().getTravelInfo().getRawTravelTime();
        }
        travelInfo.addTravelInfo(travelTimeWithParking, travelTimeWithoutParking);
    }

    private void extendLabelToAllPossibleTasks(Label label, PriorityQueue<Label> labelsOnDestinationNode) {
        boolean returnToDestinationNode = true;
        for (ExtendToInfo extendToInfo : nodeExtendInfo.extend(label)) {
            returnToDestinationNode = false;
            extendNextNode(label, extendToInfo);
        }
        if (returnToDestinationNode) {
            Label newLabel = label.extendAlong(new ExtendToInfo(graph.getOffice(), 0));
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
        newLabel = label.extendAlong(extendToInfo);
        if (newLabel != null) {
            if (labelLists.addAndReturnTrueIfAdded(newLabel.getNode(), newLabel))
                unExtendedLabels.add(newLabel);
        }
    }
}




