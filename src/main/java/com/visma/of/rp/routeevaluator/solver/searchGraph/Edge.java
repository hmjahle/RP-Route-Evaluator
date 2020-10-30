package com.visma.of.rp.routeevaluator.solver.searchGraph;

public class Edge {
    private int id;
    private Long travelTime;
    private Node fromNode;
    private Node toNode;

    protected Edge(int id, Node from, Node to) {
        this.id = id;
        this.fromNode = from;
        this.toNode = to;
    }

    public void setTravelTime(Long travelTime) {
        this.travelTime = travelTime;
    }

    public Long getTravelTime() {
        return travelTime;
    }

    public Node getFromNode() {
        return fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public int getId() {
        return id;
    }
}
