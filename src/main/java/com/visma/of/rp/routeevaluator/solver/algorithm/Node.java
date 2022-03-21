package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class Node {

    protected int nodeId;
    private ITask task;
    private int locationId;

    protected Node(int nodeId) {
        this.nodeId = nodeId;
    }

    public Node(int nodeId, ITask task, int locationId) {
        this.task = task;
        this.nodeId = nodeId;
        this.locationId = locationId;
    }

    public Node(Node other) {
        this.nodeId = other.nodeId;
        this.task = other.task;
        this.locationId = other.locationId;
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

    public int getDurationSeconds() {
        return task == null ? 0 : task.getDuration();
    }

    public int getStartTime() {
        return task == null ? 0 : task.getStartTime();
    }

    public boolean isSynced() {
        return task != null && task.isSynced();
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
