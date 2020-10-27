package com.visma.of.rp.routeevaluator.solver.searchGraph;

import com.visma.of.rp.routeevaluator.Interfaces.IPosition;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;


   public class Node {
    private ITask task;
    private IPosition address;
    private int id;
    private boolean isSynced;

    public Node(int id, ITask task, IPosition address) {
        this.id = id;
        this.task = task;
        this.address = address;
        this.isSynced = address != null && address.isOffice() || (this.task != null && this.task.isSynced());
    }


    public boolean getRequirePhysicalAppearance() {
        return task == null || task.getRequirePhysicalAppearance();
    }

    public long getDurationSeconds() {
        return task == null ? 0 : task.getDurationSeconds();
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
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public IPosition getAddress() {
        return address;
    }

    public ITask getTask() {
        return task;
    }

    public int getId() {
        return id;
    }
}
