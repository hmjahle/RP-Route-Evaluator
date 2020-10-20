package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import java.util.Set;

class DistanceSet {
    private Edge[][] edgesNodeToNode;

    DistanceSet(int nodesCapacity) {
        edgesNodeToNode = new Edge[nodesCapacity][nodesCapacity];
    }

    void update(Set<Edge> allEdges) {
        for (Edge edge : allEdges)
            this.edgesNodeToNode[edge.getFromNode().getId()][edge.getToNode().getId()] = edge;
    }

    Edge getEdge(Node node1, Node node2) {
        return edgesNodeToNode[node1.getId()][node2.getId()];
    }
}
