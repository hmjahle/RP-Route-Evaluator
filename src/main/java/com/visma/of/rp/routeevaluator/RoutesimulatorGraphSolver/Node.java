package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import probleminstance.ProblemInstance;
import probleminstance.entities.address.AddressEntity;
import probleminstance.entities.timeinterval.task.Task;

public class Node {
    private Task task;
    private AddressEntity address;
    private int id;
    private boolean isSynced;

    public Node(int id, Task task, AddressEntity address, ProblemInstance problemInstance) {
        this.id = id;
        this.task = task;
        this.address = address;
        this.isSynced = isOffice(address, problemInstance) || (this.task != null && this.task.isSynced());
    }

    private boolean isOffice(AddressEntity address, ProblemInstance problemInstance) {
        return problemInstance != null && (address.getAddress() != null) && address.getAddress().compareTo(problemInstance.getOfficeInfo().getAddress()) == 0;
    }

    boolean getRequirePhysicalAppearance() {
        return task != null ? task.getRequirePhysicalAppearance() : true;
    }


    long getDurationSeconds() {
        return task == null ? 0 : task.getDurationSeconds();
    }

    long getStartTime() {
        return task == null ? 0 : task.getStartTime();
    }

    long getEndTime() {
        return task == null ? 0 : task.getEndTime();
    }

    void setSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }

    boolean isSynced() {
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

    public AddressEntity getAddress() {
        return address;
    }

    public Task getTask() {
        return task;
    }

    public int getId() {
        return id;
    }
}
