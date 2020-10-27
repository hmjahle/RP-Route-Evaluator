package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;


import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;

/**
 * This class holds info about the node that should be extended to (toNode) and
 * which nodeSet it belongs to (extendNodeSetNo) a value of "0" indicates that it does not belong to a set.
 * This would be the destination node.
 */
public class ExtendToInfo {

    private Node toNode;
    private int extendNodeSetNumber;

    public ExtendToInfo(Node toNode, int extendNodeSetNumber) {
        this.toNode = toNode;
        this.extendNodeSetNumber = extendNodeSetNumber;
    }

    protected Node getToNode() {
        return toNode;
    }

    protected int getExtendNodeSetNumber() {
        return extendNodeSetNumber;
    }
}
