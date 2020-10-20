package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import java.util.Set;

public class EdgesOnNodesSet {
    private Edge[][] edgesOnNode;
    private int[] edgesOnNodeCnt;

    EdgesOnNodesSet(int nodesCapacity) {
        edgesOnNodeCnt = new int[nodesCapacity];
        edgesOnNode = new Edge[nodesCapacity][nodesCapacity];
    }

    void update(Set<Edge> allEdges) {
        clearAllNodes();
        for (Edge edge : allEdges) {
            edgesOnNode[edge.getFromNode().getId()][edgesOnNodeCnt[edge.getFromNode().getId()]++] = edge;
        }
    }

    Edge getEdge(Node node, int i) {
        return edgesOnNode[node.getId()][i];
    }

    int getCntEdges(Node node) {
        return edgesOnNodeCnt[node.getId()];
    }

    void addEdge(Node node, Edge edge) {
        edgesOnNode[node.getId()][edgesOnNodeCnt[node.getId()]++] = edge;
    }

    void clearAllNodes() {
        for (int i = 0; i < edgesOnNodeCnt.length; i++)
            edgesOnNodeCnt[i] = 0;
    }

    void clearNode(Node node) {
        edgesOnNodeCnt[node.getId()] = 0;
    }
}

