package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.objectives.Objective;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Edge;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;

public class Label implements Comparable<Label> {
    private SearchInfo searchInfo;
    private Label previous;
    private Node node;
    private Node physicalLocation;
    private Objective objective;
    private IResource resources;
    /**
     * The start of service of the current task.
     */
    private long currentTime;
    /**
     * Travel time from the previous location.
     */
    private long travelTime;
    private boolean closed;
    /**
     * The time from which it is possible to calculate the travel time from a physical location to the next.
     * E.g. it is the start of service time of a task + duration of all tasks performed at that location
     * or on the way to the next location.
     */
    private long canLeaveLocationAtTime;

    public Label(SearchInfo searchInfo, Label previous, Node currentNode, Node physicalLocation, Objective objective,
                 IResource resources, long currentTime, long travelTime, long canLeaveLocationAtTime) {
        this.searchInfo = searchInfo;
        this.previous = previous;
        this.node = currentNode;
        this.physicalLocation = physicalLocation;
        this.objective = objective;
        this.resources = resources;
        this.currentTime = currentTime;
        this.travelTime = travelTime;
        this.canLeaveLocationAtTime = canLeaveLocationAtTime;
        this.closed = false;
    }

    /**
     * Check if a label is being dominated by another label. Return -1 if this is dominating, 0 if equal,
     * 1 if other dominates and 2 if neither dominates.
     *
     * @param other Label being compared to this label.
     * @return Integer indicating which label is dominated.
     */
    public int dominates(Label other) {
        long currentTime = Long.compare(this.currentTime, other.currentTime);
        long canLeaveLocationAt = Long.compare(this.canLeaveLocationAtTime, other.canLeaveLocationAtTime);
        int objective = this.objective.dominates(other.objective);
        int resources = this.resources.dominates(other.resources);

        if (resources == 2)
            return 2;
        else if (currentTime == 0 && objective == 0 && resources == 0 && canLeaveLocationAt == 0)
            return 0;
        else if (currentTime <= 0 && objective <= 0 && resources <= 0 && canLeaveLocationAt <= 0)
            return -1;
        else if (currentTime >= 0 && objective >= 0 && resources >= 0 && canLeaveLocationAt >= 0)
            return 1;
        else
            return 2;
    }

    public int compareTo(Label other) {
        return Double.compare(objective.getObjectiveValue(), other.objective.getObjectiveValue());
    }

    public Label extendAlong(ExtendToInfo extendToInfo) {
        Node nextNode = extendToInfo.getToNode();
        boolean taskRequirePhysicalAppearance = nextNode.getRequirePhysicalAppearance();
        Node newLocation = findNewLocation(taskRequirePhysicalAppearance, nextNode);
        Edge potentialEdgeToTravel = potentialEdgeToTravel(nextNode, newLocation);

        long earliestStartTimeNextTask = findEarliestStartTimeNextTask(nextNode);
        long travelTime = getTravelTime(potentialEdgeToTravel);
        long arrivalTimeNextTask = calcArrivalTimeNextTask(taskRequirePhysicalAppearance, travelTime);
        long startOfServiceNextTask = Math.max(arrivalTimeNextTask, earliestStartTimeNextTask);
        long canLeaveLocationAt = updateCanLeaveLocationAt(taskRequirePhysicalAppearance, startOfServiceNextTask);

        return generateLabel(extendToInfo, newLocation,  travelTime, arrivalTimeNextTask,
                startOfServiceNextTask, canLeaveLocationAt);
    }

    private long calcArrivalTimeNextTask(boolean requirePhysicalAppearance, long travelTime) {
        long actualTravelTime = travelTime;
        if (requirePhysicalAppearance) {
            actualTravelTime = Math.max(travelTime - (currentTime - canLeaveLocationAtTime), 0);
        }
        return actualTravelTime + currentTime + node.getDurationSeconds() + searchInfo.getRobustTimeSeconds();
    }

    private long updateCanLeaveLocationAt(boolean requirePhysicalAppearance, long startOfServiceNextTask) {
        if (requirePhysicalAppearance)
            return startOfServiceNextTask;
        else {
            return canLeaveLocationAtTime + node.getDurationSeconds() + searchInfo.getRobustTimeSeconds();
        }
    }

    private Edge potentialEdgeToTravel(Node nextNode, Node newPhysicalPosition) {
        return newPhysicalPosition == physicalLocation ? null : getEdgeToNextNode(nextNode);
    }

    private Node findNewLocation(boolean requirePhysicalAppearance, Node nextNode) {
        return requirePhysicalAppearance || nextNode.getRequirePhysicalAppearance() ? nextNode : physicalLocation;
    }

    private Label generateLabel(ExtendToInfo extendToInfo, Node physicalPosition, long travelTime,
                                 long officeArrivalTime, long serviceStartTime, long canLeaveLocationAt) {

        Node nextNode = extendToInfo.getToNode();
        long arrivalTime = nextNode.getTask() == null ? officeArrivalTime : serviceStartTime;
        long earliestPossibleReturnToOfficeTime = arrivalTime + nextNode.getDurationSeconds() +
                searchInfo.getTravelTimeToOffice(nextNode);

        long syncedLatestStart = nextNode.isSynced() ? searchInfo.getSyncedNodesLatestStartTime()[nextNode.getId()] : -1;
        if (!searchInfo.isFeasible(earliestPossibleReturnToOfficeTime, nextNode.getTask(), serviceStartTime, syncedLatestStart))
            return null;

        Objective objective = this.objective.extend(this.searchInfo, nextNode, travelTime,
                serviceStartTime, officeArrivalTime, syncedLatestStart);

        IResource resources = this.resources.extend(extendToInfo);
        return new Label(this.searchInfo, this, nextNode, physicalPosition, objective, resources,
                arrivalTime,travelTime, canLeaveLocationAt);
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

    public long getCanLeaveLocationAtTime() {
        return canLeaveLocationAtTime;
    }

    public long getTravelTime() {
        return travelTime;
    }
}


