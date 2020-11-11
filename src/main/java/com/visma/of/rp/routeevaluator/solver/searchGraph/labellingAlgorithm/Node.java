package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

public class Node {

    private int id;
    private ITask task;
    private int locationId;
    private boolean isSynced; //ToDo: Test consequence of removing field

    public Node(int id, ITask task, int locationId) {
        this(id, task);
        this.locationId = locationId;
    }

    public Node(int id, ITask task) {
        this.task = task;
        this.id = id;
        this.locationId = id;
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

    long getEndTime() {
        return task == null ? 0 : task.getEndTime();
    }

    void setSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }

    public boolean isSynced() {
        return isSynced;
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Node))
            return ((Node) other).id == this.id;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public ITask getTask() {
        return task;
    }

    public int getId() {
        return id;
    }
}
