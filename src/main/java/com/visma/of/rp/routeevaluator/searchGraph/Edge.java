package com.visma.of.rp.routeevaluator.searchGraph;

import com.visma.of.rp.routeevaluator.transportInfo.TravelInfo;

public class Edge {
    private int id;
    private TravelInfo travelInfo;
    private Node fromNode;
    private Node toNode;

    protected Edge(int id, Node from, Node to) {
        this.id = id;
        this.fromNode = from;
        this.toNode = to;
    }

    public void setTravelInfo(TravelInfo travelInfo) {
        this.travelInfo = travelInfo;
    }

    public TravelInfo getTravelInfo() {
        return travelInfo;
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
