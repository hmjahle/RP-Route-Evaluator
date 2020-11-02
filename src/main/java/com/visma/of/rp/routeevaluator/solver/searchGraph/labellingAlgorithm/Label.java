package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.objectives.Objective;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Edge;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;


public class Label implements Comparable<Label> {
    private Label previous;
    private Node node;
    private Node physicalLocation;
    private Objective objective;
    private Edge edge;
    private long currentTime;
    private long extraDrivingTime;
    private IResource resources;
    private SearchInfo searchInfo;
    private boolean closed;
    private long robustTimeSeconds;
    private long actualRobustTimeSeconds;

    public Label(SearchInfo searchInfo, Label previous, Node currentNode, Node physicalLocation, Edge edge, Objective objective, long currentTime, long extraDrivingTime, IResource resources, long robustTimeSeconds) {
        this.previous = previous;
        this.edge = edge;
        this.node = currentNode;
        this.physicalLocation = physicalLocation;
        this.objective = objective;
        this.currentTime = currentTime;
        this.extraDrivingTime = extraDrivingTime;
        this.resources = resources;
        this.searchInfo = searchInfo;
        this.closed = false;
        this.robustTimeSeconds = robustTimeSeconds;
        // Do not add robustness from first visit
        this.actualRobustTimeSeconds = (previous != null ? robustTimeSeconds : 0);
    }

    //Return -1 if this is dominating, 0 if equal, 1 if other dominates and 2 if neither dominates.
    public int dominates(Label other) {
        long currentTime = Long.compare(this.currentTime, other.currentTime);
        long extraDrivingTime = Long.compare(-this.extraDrivingTime, -other.extraDrivingTime);
        int objective = this.objective.dominates(other.objective);
        int resources = this.resources.dominates(other.resources);

        if (resources == 2)
            return 2;
        else if (currentTime == 0 && objective == 0 && resources == 0 && extraDrivingTime == 0)
            return 0;
        else if (currentTime <= 0 && objective <= 0 && resources <= 0 && extraDrivingTime <= 0)
            return -1;
        else if (currentTime >= 0 && objective >= 0 && resources >= 0 && extraDrivingTime >= 0)
            return 1;
        else
            return 2;
    }

    public int compareTo(Label other) {
        return Double.compare(objective.getObjectiveValue(), other.objective.getObjectiveValue());
    }

    public Label extendAlong(ExtendToInfo extendToInfo) {
        Node nextNode = extendToInfo.getToNode();
        Node newPhysicalPosition = nextNode.getRequirePhysicalAppearance() ? nextNode : physicalLocation;
        Edge travelledEdge = newPhysicalPosition == physicalLocation ? null : getEdgeToNextNode(extendToInfo.getToNode());
        long travelTime = getTravelTime(travelledEdge);

        long savedTravelTimeDueToNonPhysicalTasks = Math.min(travelTime, extraDrivingTime);
        long baseTime = currentTime - savedTravelTimeDueToNonPhysicalTasks + node.getDurationSeconds();
        long arrivalTimeNextTask = baseTime + travelTime + actualRobustTimeSeconds;
        long earliestStartTime = getEarliestStartTime(extendToInfo.getToNode());

        return generateLabel(extendToInfo, newPhysicalPosition, travelledEdge,
                travelTime, baseTime + travelTime,
                Math.max(arrivalTimeNextTask, earliestStartTime),
                getExtraDrivingTime(extendToInfo.getToNode(), earliestStartTime, savedTravelTimeDueToNonPhysicalTasks, arrivalTimeNextTask));
    }

    private Label generateLabel(ExtendToInfo extendToInfo, Node physicalPosition, Edge travelledEdge,
                                long travelTimeWithParking, long officeArrivalTime, long serviceStartTime, long extraDrivingTime) {

        Node toNode = extendToInfo.getToNode();
        long arrivalTime = toNode.getTask() == null ? officeArrivalTime : serviceStartTime;
        long earliestPossibleReturnToOfficeTime = arrivalTime + toNode.getDurationSeconds() +
                searchInfo.getTravelTimeToOffice(toNode);

        long syncedLatestStart = toNode.isSynced() ? searchInfo.getSyncedNodesLatestStartTime()[toNode.getId()] : -1;
        if (!searchInfo.isFeasible(earliestPossibleReturnToOfficeTime, toNode.getTask(), serviceStartTime, syncedLatestStart))
            return null;

        Objective objective = this.objective.extend(this.searchInfo, toNode, travelTimeWithParking,
                serviceStartTime, officeArrivalTime, syncedLatestStart);

        IResource resources = this.resources.extend(extendToInfo);

        return new Label(this.searchInfo, this, toNode, physicalPosition, travelledEdge, objective,
                arrivalTime, extraDrivingTime, resources, robustTimeSeconds);
    }


    private long getExtraDrivingTime(Node toNode, long earliestStartTime, long savedTravelTime, long arrivalTime) {
        long extraDrivingTime = this.extraDrivingTime - savedTravelTime;

        if (!toNode.getRequirePhysicalAppearance()) {
            extraDrivingTime += (arrivalTime < earliestStartTime ? earliestStartTime - arrivalTime : 0);
        } else {
            extraDrivingTime = 0;
        }
        return extraDrivingTime;
    }

    private long getEarliestStartTime(Node toNode) {
        long earliestStartTime;
        if (toNode.isSynced()) {
            earliestStartTime = searchInfo.getSyncedNodesStartTime()[toNode.getId()];
        } else {
            earliestStartTime = toNode.getStartTime();
        }
        return earliestStartTime;
    }

    private long getTravelTime(Edge edge) {
        if (shouldNotTravel(edge)) {
            return 0;
        } else
            return edge.getTravelTime();

    }

    private Edge getEdgeToNextNode(Node toNode) {
        return searchInfo.getGraph().getEdgesNodeToNode().getEdge(this.physicalLocation, toNode);
    }

    private boolean physicallyAtCurrentNode(Node physicalPosition, Node currentNode) {
        return physicalPosition.equals(currentNode);
    }

    private boolean shouldNotTravel(Edge edge) {
        return edge == null || this.physicalLocation.equals(edge.getToNode());
    }

    public String toString() {
        return node.getId() + ", " + objective + ", " + resources;
    }

    public Label getPrevious() {
        return previous;
    }

    public Edge getEdge() {
        return edge;
    }

    public Node getNode() {
        return node;
    }

    public boolean isClosed() {
        return closed;
    }

    public Objective getObjective() {
        return objective;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public IResource getResources() {
        return resources;
    }

    public void setClosed(boolean close) {
        closed = close;
    }

    public Node getPhysicalLocation() {
        return physicalLocation;
    }

    public long getExtraDrivingTime() {
        return extraDrivingTime;
    }
}


