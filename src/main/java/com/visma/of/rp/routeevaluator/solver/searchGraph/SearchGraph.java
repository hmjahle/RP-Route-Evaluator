package com.visma.of.rp.routeevaluator.solver.searchGraph;


import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.Interfaces.ITravelTimeMatrix;

import java.util.*;

public class SearchGraph {

    private List<Node> nodes;
    private Map<ITask, Node> nodesToTask;
    private Node office;
    private Set<Edge> edgesAll;
    private TravelTimeSet edgesNodeToNode;
    private Map<Edge, Long> edgeTravelTime;
    private int nodeId;
    private int edgeId;
    private long robustTimeSeconds;

    public SearchGraph(ITravelTimeMatrix travelTimeMatrix, Collection<ITask> tasks, ILocation officePosition, long robustTimeSeconds) {
        this.robustTimeSeconds = robustTimeSeconds;
        this.nodes = new ArrayList<>();
        this.edgesAll = new HashSet<>();
        this.nodesToTask = new HashMap<>();
        this.edgeTravelTime = new HashMap<>();
        this.nodeId = 0;
        this.edgeId = 0;
        this.populateGraph(travelTimeMatrix, tasks, officePosition);
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

    private void populateGraph(ITravelTimeMatrix travelTimeMatrix, Collection<ITask> tasks, ILocation officePosition) {
        addNodesToGraph(tasks, officePosition);
        addTravelInfo(travelTimeMatrix);
        addEdges();
    }


    private void addEdges() {
        edgesNodeToNode = new TravelTimeSet(nodes.size());
        edgesNodeToNode.update(edgesAll);
    }

    private void addTravelInfo(ITravelTimeMatrix travelTimeMatrix) {
        for (Node node1 : nodes) {
            for (Node node2 : nodes) {
                if (node1 != node2) {
                    addEdge(travelTimeMatrix, node1, node2);
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

    private void addEdge(ITravelTimeMatrix travelTimeMatrix, Node node1, Node node2) {
        if (!travelTimeMatrix.connected(node1.getAddress(), node2.getAddress()))
            return;
        Edge edge = new Edge(getNewEdgeId(), node1, node2);
        Long travelTime = travelTimeMatrix.getTravelTime(node1.getAddress(), node2.getAddress());
        edge.setTravelTime(travelTime);
        edgeTravelTime.put(edge, travelTime);
        edgesAll.add(edge);
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
