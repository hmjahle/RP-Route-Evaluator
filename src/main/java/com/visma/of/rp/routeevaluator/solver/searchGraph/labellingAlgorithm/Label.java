package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.Objective;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Edge;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;

public class Label implements Comparable<Label> {
    private SearchInfo searchInfo;
    private Label previous;
    private Node node;
    private Node currentLocation;
    private Objective objective;
    private IResource resources;
    private long currentTime;
    private long travelTime;
    private long canLeaveLocationAtTime;
    private boolean closed;

    public Label(SearchInfo searchInfo, Label previous, Node currentNode, Node currentLocation, Objective objective,
                 IResource resources, long currentTime, long travelTime, long canLeaveLocationAtTime) {
        this.searchInfo = searchInfo;
        this.previous = previous;
        this.node = currentNode;
        this.currentLocation = currentLocation;
        this.objective = objective;
        this.resources = resources;
        this.currentTime = currentTime;
        this.travelTime = travelTime;
        this.canLeaveLocationAtTime = canLeaveLocationAtTime;
        this.closed = false;
    }

    public Label extendAlong(ExtendToInfo extendToInfo) {
        Node nextNode = extendToInfo.getToNode();
        boolean taskRequirePhysicalAppearance = nextNode.getRequirePhysicalAppearance();
        Node newLocation = findNewLocation(taskRequirePhysicalAppearance, nextNode);
        long travelTime = getTravelTime(nextNode, newLocation);
        long startOfServiceNextTask = calcStartOfServiceNextTask(nextNode, taskRequirePhysicalAppearance, travelTime);

        long earliestOfficeReturn = calcEarliestPossibleReturnToOfficeTime(nextNode, newLocation, startOfServiceNextTask);
        long syncedTaskLatestStartTime = nextNode.isSynced() ? searchInfo.getSyncedNodesLatestStartTime()[nextNode.getId()] : -1;
        if (!searchInfo.isFeasible(earliestOfficeReturn, nextNode.getTask(), startOfServiceNextTask, syncedTaskLatestStartTime))
            return null;

        long canLeaveLocationAt = updateCanLeaveLocationAt(taskRequirePhysicalAppearance, startOfServiceNextTask);
        return buildNewLabel(extendToInfo, nextNode, newLocation, travelTime,
                startOfServiceNextTask, canLeaveLocationAt, syncedTaskLatestStartTime);
    }

    private Label buildNewLabel(ExtendToInfo extendToInfo, Node nextNode, Node newLocation, long travelTime, long startOfServiceNextTask, long canLeaveLocationAt, long syncedTaskLatestStartTime) {
        Objective objective = this.objective.extend(this.searchInfo, nextNode, travelTime, startOfServiceNextTask, syncedTaskLatestStartTime);
        IResource resources = this.resources.extend(extendToInfo);
        return new Label(this.searchInfo, this, nextNode, newLocation, objective, resources, startOfServiceNextTask, travelTime, canLeaveLocationAt);
    }

    private long calcStartOfServiceNextTask(Node nextNode, boolean taskRequirePhysicalAppearance, long travelTime) {
        long arrivalTimeNextTask = calcArrivalTimeNextTask(taskRequirePhysicalAppearance, travelTime);
        long earliestStartTimeNextTask = findEarliestStartTimeNextTask(nextNode);
        return Math.max(arrivalTimeNextTask, earliestStartTimeNextTask);
    }

    private long getTravelTime(Node nextNode, Node newLocation) {
        Edge edge = newLocation == currentLocation ? null : getEdgeToNextNode(nextNode);
        if (edge == null) {
            return 0;
        } else
            return edge.getTravelTime();
    }

    private long calcEarliestPossibleReturnToOfficeTime(Node nextNode, Node currentLocation, long startOfServiceNextTask) {
        return startOfServiceNextTask + nextNode.getDurationSeconds() + searchInfo.getTravelTimeToOffice(currentLocation);
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

    private Node findNewLocation(boolean requirePhysicalAppearance, Node nextNode) {
        return requirePhysicalAppearance || nextNode.getRequirePhysicalAppearance() ? nextNode : currentLocation;
    }

    private long findEarliestStartTimeNextTask(Node toNode) {
        if (toNode.isSynced()) {
            return searchInfo.getSyncedNodesStartTime()[toNode.getId()];
        } else {
            return toNode.getStartTime();
        }
    }

    private Edge getEdgeToNextNode(Node toNode) {
        return searchInfo.getGraph().getEdgesNodeToNode().getEdge(this.currentLocation, toNode);
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

    /**
     * The start of service of the current task.
     */
    public long getCurrentTime() {
        return currentTime;
    }

    public IResource getResources() {
        return resources;
    }

    public void setClosed(boolean close) {
        closed = close;
    }

    public Node getCurrentLocation() {
        return currentLocation;
    }

    /**
     * The time from which it is possible to calculate the travel time from a physical location to the next.
     * E.g. it is the start of service time of a task + duration of all tasks performed at that location
     * or on the way to the next location.
     */
    public long getCanLeaveLocationAtTime() {
        return canLeaveLocationAtTime;
    }

    /**
     * Travel time from the previous location.
     */
    public long getTravelTime() {
        return travelTime;
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

    @Override
    public String toString() {
        return node.getId() + ", " + objective + ", " + resources;
    }

}


