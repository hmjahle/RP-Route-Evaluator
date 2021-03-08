package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

/**
 * Unknown location nodes can be used in the labelling algorithms as origin and destination nodes where the location is
 * not yet determined. This can be in the case where the route has to start at the first task or end at the last task.
 */
public class UnknownLocationNode extends Node {

    public UnknownLocationNode(int nodeId) {
        super(nodeId);
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
    public ITask getTask() {
        return null;
    }

    @Override
    public int hashCode() {
        return nodeId;
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof UnknownLocationNode))
            return ((UnknownLocationNode) other).nodeId == this.nodeId;
        else
            return false;
    }
}
