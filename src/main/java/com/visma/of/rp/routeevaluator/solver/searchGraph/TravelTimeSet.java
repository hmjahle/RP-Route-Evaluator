package com.visma.of.rp.routeevaluator.solver.searchGraph;

import java.util.Set;

 public class TravelTimeSet {
    private Edge[][] edgesNodeToNode;

    public TravelTimeSet(int nodesCapacity) {
        edgesNodeToNode = new Edge[nodesCapacity][nodesCapacity];
    }

    public void update(Set<Edge> allEdges) {
        for (Edge edge : allEdges)
            this.edgesNodeToNode[edge.getFromNode().getId()][edge.getToNode().getId()] = edge;
    }

    public Edge getEdge(Node node1, Node node2) {
        return edgesNodeToNode[node1.getId()][node2.getId()];
    }
}
