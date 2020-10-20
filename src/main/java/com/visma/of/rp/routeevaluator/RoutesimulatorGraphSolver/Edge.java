package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import routeplanner.solvers.fitness.entities.TravelInfo;

class Edge {
    private int id;
    private TravelInfo travelInfo;
    private Node fromNode;
    private Node toNode;

    Edge(int id, Node from, Node to) {
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
