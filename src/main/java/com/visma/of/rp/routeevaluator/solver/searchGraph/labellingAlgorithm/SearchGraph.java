package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;

import java.util.*;

public class SearchGraph {

    private Node origin;
    private Node destination;
    private List<Node> nodes;
    private Map<ITask, Node> nodesToTask;
    private Long[][] travelTimeMatrix;
    private int nodeIdCounter;
    private long robustTimeSeconds;
    private Map<ILocation, Integer> locationToLocationIds;

    public SearchGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<ITask> tasks, ILocation originLocation, ILocation destinationLocation, long robustTimeSeconds) {
        this.robustTimeSeconds = robustTimeSeconds;
        this.nodes = new ArrayList<>();
        this.nodesToTask = new HashMap<>();
        this.nodeIdCounter = 0;
        this.populateGraph(travelTimeMatrixInput, tasks, originLocation, destinationLocation);
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

    private void populateGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<ITask> tasks, ILocation startLocation, ILocation destinationLocation) {
        this.origin = new Node(getNewNodeId(), null);
        this.destination = new Node(getNewNodeId(), null);

        addNodesToGraph(tasks);
        int n = nodes.size() + 2;
        this.travelTimeMatrix = new Long[n][n];
        addTravelInfo(travelTimeMatrixInput);
        locationToLocationIds = new HashMap<>();
        addTravelTimeBetweenNodes(travelTimeMatrixInput, startLocation, origin.getId(), destinationLocation, destination.getId());
        addTravelTimeBetweenNodes(travelTimeMatrixInput, destinationLocation, destination.getId(), startLocation, origin.getId());

        for (Node nodeA : nodes) {
            locationToLocationIds.put(nodeA.getTask().getLocation(), nodeA.getId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, startLocation, origin.getId(), nodeA.getTask().getLocation(), nodeA.getId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, destinationLocation, destination.getId(), nodeA.getTask().getLocation(), nodeA.getId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, nodeA.getTask().getLocation(), nodeA.getId(), startLocation, origin.getId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, nodeA.getTask().getLocation(), nodeA.getId(), destinationLocation, destination.getId());
        }
        locationToLocationIds.put(startLocation, origin.getId());
        locationToLocationIds.put(destinationLocation, destination.getId());
        nodes.add(origin);
        nodes.add(destination);
    }

    private void addTravelInfo(ITravelTimeMatrix travelTimeMatrixInput) {
        for (Node nodeA : nodes) {
            for (Node nodeB : nodes) {
                if (nodeA != nodeB) {
                    addTravelTimeBetweenNodes(travelTimeMatrixInput, nodeA.getTask().getLocation(), nodeA.getId(),

                            nodeB.getTask().getLocation(), nodeB.getId());
                }
            }
        }
    }

    private void addNodesToGraph(Collection<ITask> tasks) {
        for (ITask task : tasks) {
            Node node = new Node(getNewNodeId(), task);
            nodes.add(node);
            nodesToTask.put(task, node);
        }
    }

    private void addTravelTimeBetweenNodes(ITravelTimeMatrix travelTimeMatrixInput, ILocation fromLocation, int fromId,
                                           ILocation toLocation, int toId) {
        if (!travelTimeMatrixInput.connected(fromLocation, toLocation))
            return;
        long travelTime = travelTimeMatrixInput.getTravelTime(fromLocation, toLocation);
        this.travelTimeMatrix[fromId][toId] = travelTime;
    }

    public void updateNodeType(ITask task) {
        nodesToTask.get(task).setSynced(task.isSynced());
    }

    public void updateNodeType(Collection<ITask> tasks) {
        for (ITask task : tasks)
            nodesToTask.get(task).setSynced(task.isSynced());
    }

    public Node getOrigin() {
        return origin;
    }

    public Node getDestination() {
        return destination;
    }

    public Node getNode(ITask task) {
        return nodesToTask.get(task);
    }

    public long getRobustTimeSeconds() {
        return robustTimeSeconds;
    }
}
