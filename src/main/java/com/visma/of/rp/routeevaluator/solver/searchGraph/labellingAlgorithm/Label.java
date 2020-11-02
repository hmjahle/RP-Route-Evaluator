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
    private long timeAlreadyTravelled;
    private IResource resources;
    private SearchInfo searchInfo;
    private boolean closed;
    private long robustTimeSeconds;
    private long actualRobustTimeSeconds;

    public long getCanLeaveLocationAt() {
        return canLeaveLocationAt;
    }

    public void setCanLeaveLocationAt(long canLeaveLocationAt) {
        this.canLeaveLocationAt = canLeaveLocationAt;
    }

    private long canLeaveLocationAt;

    public long getConsumedTime() {
        return consumedTime;
    }

    public void setConsumedTime(long consumedTime) {
        this.consumedTime = consumedTime;
    }

    private long consumedTime;

    public Label(SearchInfo searchInfo, Label previous, Node currentNode, Node physicalLocation, Edge edge, Objective objective, long currentTime, long timeAlreadyTravelled, IResource resources, long robustTimeSeconds) {
        this.previous = previous;
        this.edge = edge;
        this.node = currentNode;
        this.physicalLocation = physicalLocation;
        this.objective = objective;
        this.currentTime = currentTime;
        this.timeAlreadyTravelled = timeAlreadyTravelled;
        this.resources = resources;
        this.searchInfo = searchInfo;
        this.closed = false;
        this.robustTimeSeconds = robustTimeSeconds;
        this.actualRobustTimeSeconds = (previous != null ? robustTimeSeconds : 0);

        this.canLeaveLocationAt = currentTime;

    }


    /**
     * Check if a label is being dominated by another label. Return -1 if this is dominating, 0 if equal,
     * 1 if other dominates and 2 if neither dominates.
     *
     * @param other Label being compared to this label.
     * @return Integer indicating which label is dominated.
     */
    //Return
    public int dominates(Label other) {
        long currentTime = Long.compare(this.currentTime, other.currentTime);
        long extraDrivingTime = Long.compare(-this.timeAlreadyTravelled, -other.timeAlreadyTravelled);
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
        boolean requirePhysicalAppearance = nextNode.getRequirePhysicalAppearance();
        Node newPhysicalPosition = newPhysicalLocation(requirePhysicalAppearance, nextNode);
        Edge potentialEdgeToTravel = potentialEdgeToTravel(nextNode, newPhysicalPosition);

        long earliestStartTimeNextTask = findEarliestStartTimeNextTask(nextNode);
        long travelTime = getTravelTime(potentialEdgeToTravel);
        long actualTravelTime = travelTime;
        long extraTimeAvailable = 0;
        long newConsumedTime = consumedTime;
        long newCanLeaveLocationAt = canLeaveLocationAt;

        if (requirePhysicalAppearance) {
            extraTimeAvailable = currentTime - canLeaveLocationAt - consumedTime;
            actualTravelTime = Math.max(travelTime - extraTimeAvailable, 0);
            newConsumedTime = 0;
        } else {
            newCanLeaveLocationAt += node.getDurationSeconds() + actualRobustTimeSeconds;
        }
        long taskFinishedAt = currentTime + node.getDurationSeconds();
        System.out.println("actualTravelTime " + actualTravelTime);
        long arrivalTimeNextTask = actualTravelTime + taskFinishedAt + actualRobustTimeSeconds;

        long startOfServiceNextTask = Math.max(arrivalTimeNextTask, earliestStartTimeNextTask);

        if (requirePhysicalAppearance)
             newCanLeaveLocationAt = startOfServiceNextTask;


        System.out.println("Next require: " + requirePhysicalAppearance);

        System.out.println("currentTime: " + currentTime);
        System.out.println("extraTimeAvailable: " + extraTimeAvailable);
        System.out.println("canLeaveLocationAt: " + canLeaveLocationAt);
        System.out.println("node.getDurationSeconds(): " + node.getDurationSeconds());
        System.out.println("consumedTime: " + consumedTime);
        System.out.println("travelTime: " + travelTime);
        System.out.println("actualTravelTime22: " + actualTravelTime);
        System.out.println();

        Label newLabel = generateLabel(extendToInfo, newPhysicalPosition, potentialEdgeToTravel, travelTime, arrivalTimeNextTask,
                startOfServiceNextTask, 0);
        newLabel.setCanLeaveLocationAt(newCanLeaveLocationAt);
        newLabel.setConsumedTime(newConsumedTime);
        return newLabel;
//        Label newLabel = generateLabel(extendToInfo, newPhysicalPosition, potentialEdgeToTravel, travelTime, arrivalTimeNextTask,
//                startOfServiceNextTask, updatedTimeAlreadyTravelled);
//        newLabel.setCanLeaveLocationAt(newcanLeaveLocationAt);
//        newLabel.setConsumedTime(newConsumedTime);
//        return newLabel;

    }

    private long calculateUpdatedTimeAlreadyTravelled(boolean requirePhysicalAppearance, long actualTravelTime, long arrivalTimeNextTask, long startOfServiceNextTask) {
        if (requirePhysicalAppearance)
            return 0;
        else if (actualTravelTime > 0)
            return 0;
        else
            return this.timeAlreadyTravelled + startOfServiceNextTask - arrivalTimeNextTask;
    }

    private Edge potentialEdgeToTravel(Node nextNode, Node newPhysicalPosition) {
        return newPhysicalPosition == physicalLocation ? null : getEdgeToNextNode(nextNode);
    }

    private Node newPhysicalLocation(boolean requirePhysicalAppearance, Node nextNode) {
        return requirePhysicalAppearance || nextNode.getRequirePhysicalAppearance() ? nextNode : physicalLocation;
    }

    private Label generateLabel(ExtendToInfo extendToInfo, Node physicalPosition, Edge travelledEdge,
                                long travelTimeWithParking, long officeArrivalTime, long serviceStartTime, long extraDrivingTime) {

        Node nextNode = extendToInfo.getToNode();
        long arrivalTime = nextNode.getTask() == null ? officeArrivalTime : serviceStartTime;
        long earliestPossibleReturnToOfficeTime = arrivalTime + nextNode.getDurationSeconds() +
                searchInfo.getTravelTimeToOffice(nextNode);

        long syncedLatestStart = nextNode.isSynced() ? searchInfo.getSyncedNodesLatestStartTime()[nextNode.getId()] : -1;
        if (!searchInfo.isFeasible(earliestPossibleReturnToOfficeTime, nextNode.getTask(), serviceStartTime, syncedLatestStart))
            return null;

        Objective objective = this.objective.extend(this.searchInfo, nextNode, travelTimeWithParking,
                serviceStartTime, officeArrivalTime, syncedLatestStart);

        IResource resources = this.resources.extend(extendToInfo);

        return new Label(this.searchInfo, this, nextNode, physicalPosition, travelledEdge, objective,
                arrivalTime, extraDrivingTime, resources, robustTimeSeconds);
    }


    private long findEarliestStartTimeNextTask(Node toNode) {
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

    public long getTimeAlreadyTravelled() {
        return timeAlreadyTravelled;
    }
}


