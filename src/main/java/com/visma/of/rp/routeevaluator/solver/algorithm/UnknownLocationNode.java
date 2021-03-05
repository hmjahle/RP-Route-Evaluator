package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

public class UnknownLocationNode extends Node {

    public UnknownLocationNode() {
        super();
    }

    @Override
    public int getLocationId() {
        return -1;
    }

    @Override

    public boolean getRequirePhysicalAppearance() {
        return false;
    }

    @Override

    public int getDurationSeconds() {
        return 0;
    }

    @Override

    public int getStartTime() {
        return 0;
    }

    @Override
    public boolean isSynced() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnknownLocationNode;
    }

    @Override
    public int hashCode() {
        return 313546544 << 3;
    }

    @Override
    public String toString() {
        return Integer.toString(nodeId);
    }

    @Override
    public ITask getTask() {
        return null;
    }

    @
    public int getNodeId() {
        return nodeId;
    }
}
