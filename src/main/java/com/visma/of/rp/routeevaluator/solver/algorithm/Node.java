package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class Node {

    private int nodeId;
    private ITask task;
    private int locationId;
    private boolean isSynced;

    public Node(int nodeId, ITask task, int locationId) {
        this.task = task;
        this.nodeId = nodeId;
        this.locationId = locationId;
        this.isSynced = (this.task != null && this.task.isSynced());
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public boolean getRequirePhysicalAppearance() {
        return task == null || task.getRequirePhysicalAppearance();
    }

    public long getDurationSeconds() {
        return task == null ? 0 : task.getDuration();
    }

    public long getStartTime() {
        return task == null ? 0 : task.getStartTime();
    }

    public boolean isSynced() {
        return isSynced;
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Node))
            return ((Node) other).nodeId == this.nodeId;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return nodeId;
    }

    @Override
    public String toString() {
        return Integer.toString(nodeId);
    }

    public ITask getTask() {
        return task;
    }

    public int getNodeId() {
        return nodeId;
    }
}
