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

    /**
     * Location must be present in the route evaluator, i.e.,
     * the travel times matrix given when the route evaluator was constructed.
     *
     * @param originLocation The the location where the route should start.
     */
    public void updateOrigin(ILocation originLocation) {
        origin.setLocationId(locationToLocationIds.get(originLocation));
    }

    /**
     * Location must be present in the route evaluator, i.e.,
     * the travel times matrix given when the route evaluator was constructed.
     *
     * @param destinationLocation The the location where the route should end.
     */
    public void updateDestination(ILocation destinationLocation) {
        destination.setLocationId(locationToLocationIds.get(destinationLocation));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Long getTravelTime(Node nodeA, Node nodeB) {
        return travelTimeMatrix[nodeA.getLocationId()][nodeB.getLocationId()];
    }

    private int getNewNodeId() {
        return nodeIdCounter++;
    }

    private void populateGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<ITask> tasks, ILocation startLocation, ILocation destinationLocation) {
        this.origin = new Node(getNewNodeId(), null);
        this.destination = new Node(getNewNodeId(), null);
        addNodesToGraph(tasks);
        updateTravelTimeInformation(travelTimeMatrixInput);
        connectOriginDestinationNodes(travelTimeMatrixInput, startLocation, destinationLocation);
    }

    private void connectOriginDestinationNodes(ITravelTimeMatrix travelTimeMatrixInput, ILocation startLocation, ILocation destinationLocation) {
        addTravelTimeBetweenNodes(travelTimeMatrixInput, startLocation, origin.getLocationId(), destinationLocation, destination.getLocationId());
        addTravelTimeBetweenNodes(travelTimeMatrixInput, destinationLocation, destination.getLocationId(), startLocation, origin.getLocationId());
        connectNodesToOriginDestination(travelTimeMatrixInput, startLocation, destinationLocation);
        locationToLocationIds.put(startLocation, origin.getLocationId());
        locationToLocationIds.put(destinationLocation, destination.getLocationId());
        nodes.add(origin);
        nodes.add(destination);
    }

    private void connectNodesToOriginDestination(ITravelTimeMatrix travelTimeMatrixInput, ILocation startLocation, ILocation destinationLocation) {
        for (Node nodeA : nodes) {
            addTravelTimeBetweenNodes(travelTimeMatrixInput, startLocation, origin.getLocationId(), nodeA.getTask().getLocation(), nodeA.getLocationId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, destinationLocation, destination.getLocationId(), nodeA.getTask().getLocation(), nodeA.getLocationId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, nodeA.getTask().getLocation(), nodeA.getLocationId(), startLocation, origin.getLocationId());
            addTravelTimeBetweenNodes(travelTimeMatrixInput, nodeA.getTask().getLocation(), nodeA.getLocationId(), destinationLocation, destination.getLocationId());
        }
    }

    private void updateTravelTimeInformation(ITravelTimeMatrix travelTimeMatrixInput) {
        int n = nodes.size() + 2;
        this.travelTimeMatrix = new Long[n][n];
        this.locationToLocationIds = new HashMap<>();
        for (Node nodeA : nodes) {
            locationToLocationIds.put(nodeA.getTask().getLocation(), nodeA.getLocationId());
            for (Node nodeB : nodes) {
                if (nodeA != nodeB) {
                    addTravelTimeBetweenNodes(travelTimeMatrixInput, nodeA.getTask().getLocation(), nodeA.getLocationId(),
                            nodeB.getTask().getLocation(), nodeB.getLocationId());
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
