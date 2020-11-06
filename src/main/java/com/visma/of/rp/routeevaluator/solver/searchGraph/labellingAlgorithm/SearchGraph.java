package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;


import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;

import java.util.*;

public class SearchGraph {

    private Node office;
    private List<Node> nodes;
    private Map<ITask, Node> nodesToTask;
    private Long[][] travelTimeMatrix;
    private int nodeIdCounter;
    private long robustTimeSeconds;

    public SearchGraph(ITravelTimeMatrix travelTimeMatrix, Collection<ITask> tasks, ILocation officePosition, long robustTimeSeconds) {
        this.robustTimeSeconds = robustTimeSeconds;
        this.nodes = new ArrayList<>();
        this.nodesToTask = new HashMap<>();
        this.nodeIdCounter = 0;
        this.populateGraph(travelTimeMatrix, tasks, officePosition);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Long getTravelTime(Node nodeA, Node nodeB) {
        return travelTimeMatrix[nodeA.getId()][nodeB.getId()];
    }

    private int getNewNodeId() {
        return nodeIdCounter++;
    }

    private void populateGraph(ITravelTimeMatrix travelTimeMatrix, Collection<ITask> tasks, ILocation officePosition) {
        addNodesToGraph(tasks, officePosition);
        this.travelTimeMatrix = new Long[nodes.size()][nodes.size()];
        addTravelInfo(travelTimeMatrix);
    }

    private void addTravelInfo(ITravelTimeMatrix travelTimeMatrix) {
        for (Node nodeA : nodes) {
            for (Node nodeB : nodes) {
                if (nodeA != nodeB) {
                    addTravelTimeBetweenNodes(travelTimeMatrix, nodeA, nodeB);
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

    private void addTravelTimeBetweenNodes(ITravelTimeMatrix travelTimeMatrix, Node node1, Node node2) {
        if (!travelTimeMatrix.connected(node1.getAddress(), node2.getAddress()))
            return;
        long travelTime = travelTimeMatrix.getTravelTime(node1.getAddress(), node2.getAddress());
        this.travelTimeMatrix[node1.getId()][node2.getId()] = travelTime;
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
