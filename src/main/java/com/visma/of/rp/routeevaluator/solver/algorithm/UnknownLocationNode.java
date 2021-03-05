package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

/**
 * Unknown location nodes can be used in the labelling algorithms as origin and destination nodes where the location is
 * not yet determined. This can be in the case where the route has to start at the first task or end at the last task.
 */
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

    /**
     * The hash code is overridden as the Node hash code is the node id. Hence a large negative number will likely not
     * collide with the node id of any nodes.
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return -313546547;
    }

    @Override
    public String toString() {
        return Integer.toString(nodeId);
    }

    @Override
    public ITask getTask() {
        return null;
    }

    @Override
    public int getNodeId() {
        return nodeId;
    }
}
