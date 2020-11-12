package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.Objective;

public class Label implements Comparable<Label> {
    private Label previous;
    private Node node;
    private Objective objective;
    private IResource resources;
    private int currentLocationId;
    private long currentTime;
    private long travelTime;
    private long canLeaveLocationAtTime;
    private boolean closed;

    public Label(Label previous, Node currentNode, int currentLocationId, Objective objective,
                 IResource resources, long currentTime, long travelTime, long canLeaveLocationAtTime) {
        this.previous = previous;
        this.node = currentNode;
        this.objective = objective;
        this.resources = resources;
        this.currentLocationId = currentLocationId;
        this.currentTime = currentTime;
        this.travelTime = travelTime;
        this.canLeaveLocationAtTime = canLeaveLocationAtTime;
        this.closed = false;
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

    public void close(boolean close) {
        closed = close;
    }

    public int getCurrentLocationId() {
        return currentLocationId;
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
        return node.getNodeId() + ", " + objective + ", " + resources;
    }

}


