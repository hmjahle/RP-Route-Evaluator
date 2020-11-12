package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;

import java.util.*;

public class SearchGraph {

    private Node origin;
    private Node destination;
    private List<Node> nodes;
    private Map<ITask, Node> taskToNodes;
    private Long[][] travelTimeMatrix;
    private int nodeIdCounter;
    private int locationIdCounter;
    private long robustTimeSeconds;
    private Map<ILocation, Integer> locationToLocationIds;

    public SearchGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<ITask> tasks, ILocation originLocation, ILocation destinationLocation, long robustTimeSeconds) {
        this.robustTimeSeconds = robustTimeSeconds;
        this.nodes = new ArrayList<>();
        this.taskToNodes = new HashMap<>();
        this.locationToLocationIds = new HashMap<>();
        this.nodeIdCounter = 0;
        this.locationIdCounter = 0;

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

    private int getNewLocationId() {
        return locationIdCounter++;
    }

    private void populateGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<ITask> tasks, ILocation originLocation,
                               ILocation destinationLocation) {
        initializeOriginDestination(originLocation, destinationLocation);
        addNodesToGraph(tasks);
        updateTravelTimeInformation(travelTimeMatrixInput);
    }

    private void initializeOriginDestination(ILocation originLocation, ILocation destinationLocation) {
        this.origin = new Node(getNewNodeId(), null, getLocationId(originLocation));
        this.destination = new Node(getNewNodeId(), null, getLocationId(destinationLocation));
        nodes.add(origin);
        nodes.add(destination);
        locationToLocationIds.put(originLocation, origin.getLocationId());
        locationToLocationIds.put(destinationLocation, destination.getLocationId());
    }

    private void addNodesToGraph(Collection<ITask> tasks) {
        for (ITask task : tasks) {
            int locationId = getLocationId(task.getLocation());
            Node node = new Node(getNewNodeId(), task, locationId);
            nodes.add(node);
            taskToNodes.put(task, node);
        }
    }

    private void updateTravelTimeInformation(ITravelTimeMatrix travelTimeMatrixInput) {
        int n = potentialLocations(travelTimeMatrixInput);
        this.travelTimeMatrix = new Long[n][n];
        for (ILocation locationA : travelTimeMatrixInput.getLocations()) {
            for (ILocation locationB : travelTimeMatrixInput.getLocations()) {
                if (locationA != locationB) {
                    addTravelTime(travelTimeMatrixInput, locationA, locationB);
                }
            }
        }
    }

    private int potentialLocations(ITravelTimeMatrix travelTimeMatrixInput) {
        int n = locationIdCounter;
        for (ILocation locationA : travelTimeMatrixInput.getLocations())
            n += !locationToLocationIds.containsKey(locationA) ? 1 : 0;
        return n;
    }

    private int getLocationId(ILocation location) {

        if (locationToLocationIds.containsKey(location))
            return locationToLocationIds.get(location);
        int locationId = getNewLocationId();
        locationToLocationIds.put(location, locationId);
        return locationId;
    }

    private void addTravelTime(ITravelTimeMatrix travelTimeMatrixInput, ILocation fromLocation, ILocation toLocation) {

        int fromId = getLocationId(fromLocation);
        int toId = getLocationId(toLocation);

        if (!travelTimeMatrixInput.connected(fromLocation, toLocation))
            return;
        long travelTime = travelTimeMatrixInput.getTravelTime(fromLocation, toLocation);
        this.travelTimeMatrix[fromId][toId] = travelTime;
    }

    public void updateNodeType(ITask task) {
        taskToNodes.get(task).setSynced(task.isSynced());
    }

    public void updateNodeType(Collection<ITask> tasks) {
        for (ITask task : tasks)
            taskToNodes.get(task).setSynced(task.isSynced());
    }

    public Node getOrigin() {
        return origin;
    }

    public Node getDestination() {
        return destination;
    }

    public Node getNode(ITask task) {
        return taskToNodes.get(task);
    }

    public long getRobustTimeSeconds() {
        return robustTimeSeconds;
    }
}
