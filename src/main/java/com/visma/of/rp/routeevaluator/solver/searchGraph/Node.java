package com.visma.of.rp.routeevaluator.solver.searchGraph;

import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;


public class Node {
    private ITask task;
    private ILocation address;
    private int id;
    private boolean isSynced;

    public Node(int id, ITask task, ILocation address) {
        this.id = id;
        this.task = task;
        this.address = address;
        this.isSynced = address != null && address.isOffice() || (this.task != null && this.task.isSynced());
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

    public ILocation getAddress() {
        return address;
    }

    public ITask getTask() {
        return task;
    }

    public int getId() {
        return id;
    }
}
