package com.visma.of.rp.routeevaluator.solver.algorithm;


public class Label implements Comparable<Label> {
    private Label previous;
    private Node node;
    private IObjective objective;
    private IResource resources;
    private int currentLocationId;
    private long currentTime;
    private long travelTime;
    private long canLeaveLocationAtTime;
    private boolean closed;

    public void setCanLeaveLocationAtTime(long canLeaveLocationAtTime) {
        this.canLeaveLocationAtTime = canLeaveLocationAtTime;
    }

    public Label(Label previous, Node currentNode, int currentLocationId, IObjective objective,
                 IResource resources, long currentTime, long travelTime) {
        this.previous = previous;
        this.node = currentNode;
        this.objective = objective;
        this.resources = resources;
        this.currentLocationId = currentLocationId;
        this.currentTime = currentTime;
        this.canLeaveLocationAtTime = currentTime;
        this.travelTime = travelTime;
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

    public IObjective getObjective() {
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

    public void close() {
        closed = true;
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
        int compResources = this.resources.dominates(other.resources);
        if (compResources == 2)
            return 2;

        int compCurrentTime = Long.compare(this.currentTime, other.currentTime);
        int compCanLeaveLocationAtTime = Long.compare(this.canLeaveLocationAtTime, other.canLeaveLocationAtTime);
        int compObjective = this.objective.dominates(other.objective);

        if (compCurrentTime == 0 && compObjective == 0 && compResources == 0 && compCanLeaveLocationAtTime == 0)
            return 0;
        else if (compCurrentTime <= 0 && compObjective <= 0 && compResources <= 0 && compCanLeaveLocationAtTime <= 0)
            return -1;
        else if (compCurrentTime >= 0 && compObjective >= 0 && compResources >= 0 && compCanLeaveLocationAtTime >= 0)
            return 1;
        else
            return 2;
    }


    @Override
    public int compareTo(Label other) {
        return Double.compare(objective.getObjectiveValue(), other.objective.getObjectiveValue());
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof Label)
            return this.dominates(((Label) other)) == 0;
        return false;
    }


    @Override
    public int hashCode() {
        int hash = (int) currentTime;
        hash += ((int) canLeaveLocationAtTime << 2);
        hash += ((int) travelTime << 4);
        hash += node != null ? (node.getNodeId() << 6) : 0;
        hash += node != null ? (node.getLocationId() << 8) : 0;
        hash += objective != null ? ((int) objective.getObjectiveValue() << 10) : 0;
        hash += resources != null ? resources.hashCode() : 0;
        return hash;
    }

    @Override
    public String toString() {
        int nodeId = node != null ? node.getNodeId() : -1;
        double objectiveValue = objective != null ? objective.getObjectiveValue() : 0.0;
        String resourcesString = (resources != null ? " " + resources.toString() : "");
        return nodeId + ", " + objectiveValue + resourcesString;
    }

}


