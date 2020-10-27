package com.visma.of.rp.routeevaluator.solver.searchGraph;


import com.visma.of.rp.routeevaluator.Interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;
import com.visma.of.rp.routeevaluator.transportInfo.TravelInfo;

import java.util.*;

public class SearchGraph {

    private List<Node> nodes;
    private Map<ITask, Node> nodesToTask;
    private Node office;
    private Set<Edge> edgesAll;
    private TravelTimeSet edgesNodeToNode;
    private Map<Edge, Map<TransportMode, TravelInfo>> edgeTravelInfo;
    private int nodeId;
    private int edgeId;
    private TransportMode currentTransportMode;
    private long robustTimeSeconds;

    public SearchGraph(Map<TransportMode, ITravelTimeMatrix> travelTimeMatrixMatrices, Collection<ITask> tasks, ILocation officePosition, long robustTimeSeconds) {
        this.robustTimeSeconds = robustTimeSeconds;
        this.nodes = new ArrayList<>();
        this.edgesAll = new HashSet<>();
        this.nodesToTask = new HashMap<>();
        this.edgeTravelInfo = new HashMap<>();
        this.nodeId = 0;
        this.edgeId = 0;
        this.populateGraph(travelTimeMatrixMatrices, tasks, officePosition);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public TravelTimeSet getEdgesNodeToNode() {
        return edgesNodeToNode;
    }

    private int getNewNodeId() {
        return nodeId++;
    }

    private int getNewEdgeId() {
        return edgeId++;
    }

    private void populateGraph(Map<TransportMode, ITravelTimeMatrix> travelTimeMatrixMatrices, Collection<ITask> tasks, ILocation officePosition) {
        addNodesToGraph(tasks, officePosition);
        addTravelInfo(travelTimeMatrixMatrices);
        addEdges();
    }

    public void setEdgesTransportMode(TransportMode transportMode) {
        if (transportMode != currentTransportMode) {
            currentTransportMode = transportMode;
            for (Edge edge : edgeTravelInfo.keySet()) {
                edge.setTravelInfo(edgeTravelInfo.get(edge).get(currentTransportMode));
            }
        }
    }

    private void addEdges() {
        edgesNodeToNode = new TravelTimeSet(nodes.size());
        edgesNodeToNode.update(edgesAll);
    }

    private void addTravelInfo(Map<TransportMode, ITravelTimeMatrix> travelTimeMatrixMatrices) {
        for (Node node1 : nodes) {
            for (Node node2 : nodes) {
                if (node1 != node2) {
                    addEdge(travelTimeMatrixMatrices, node1, node2);
                }
            }
        }
    }

    private void addNodesToGraph(Collection<ITask> tasks, ILocation officePosition) {
        office = new Node(getNewNodeId(), null, officePosition);
        nodes.add(office);
        for (ITask task : tasks) {
            Node node = new Node(getNewNodeId(), task, task.getLocation());
            nodes.add(node);
            nodesToTask.put(task, node);
        }
    }

    private void addEdge(Map<TransportMode, ITravelTimeMatrix> travelTimeMatrixMatrices, Node node1, Node node2) {
        Map<TransportMode, TravelInfo> eligibleTransportModes = new HashMap<>();
        for (TransportMode transportMode : travelTimeMatrixMatrices.keySet()) {
            if (travelTimeMatrixMatrices.get(transportMode).connected(node1.getAddress(), node2.getAddress())) {
                if (!node1.getRequirePhysicalAppearance() || !node2.getRequirePhysicalAppearance()) {
                    eligibleTransportModes.put(transportMode, new TravelInfo(0, 0));
                } else {
                    eligibleTransportModes.put(transportMode, getTravelInfo(travelTimeMatrixMatrices, transportMode, node1, node2));
                }
            }
        }
        Edge edge = new Edge(getNewEdgeId(), node1, node2);
        edgeTravelInfo.put(edge, eligibleTransportModes);
        edgesAll.add(edge);
    }


    private TravelInfo getTravelInfo(Map<TransportMode, ITravelTimeMatrix> travelTimeMatrixMatrices, TransportMode transportMode, Node from, Node to) {
        return new TravelInfo(
                travelTimeMatrixMatrices.get(transportMode).getTravelTime(from.getAddress(), to.getAddress()),
                travelTimeMatrixMatrices.get(transportMode).getTravelTime(from.getAddress(), to.getAddress()));
    }

    public void updateNodeType(ITask task) {
        nodesToTask.get(task).setSynced(task.isSynced());
    }

    public void updateNodeType(Collection<ITask> tasks) {
        for (ITask task : tasks)
            nodesToTask.get(task).setSynced(task.isSynced());
    }

    public Node getOffice() {
        return office;
    }

    public Node getNode(ITask task) {
        return nodesToTask.get(task);
    }

    public long getRobustTimeSeconds() {
        return robustTimeSeconds;
    }
}
