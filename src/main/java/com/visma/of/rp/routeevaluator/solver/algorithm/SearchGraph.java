package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;

import java.util.*;

public class SearchGraph {

    private Node origin;
    private Node destination;
    private List<Node> nodes;
    private Map<ITask, Node> taskToNodes;
    private Integer[][] travelTimeMatrix;
    private int nodeIdCounter;
    private int sourceId;
    private int sinkId;
    private int locationIdCounter;
    private Map<ILocation, Integer> locationToLocationIds;

    public SearchGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<? extends ITask> tasks,
                       ILocation originLocation, ILocation destinationLocation) {
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

    public Integer getTravelTime(int locationIdA, int locationIdB) {
        return travelTimeMatrix[locationIdA][locationIdB];
    }

    private int getNewNodeId() {
        return nodeIdCounter++;
    }

    private int getNewLocationId() {
        return locationIdCounter++;
    }

    private void populateGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<? extends ITask> tasks, ILocation originLocation,
                               ILocation destinationLocation) {
        updateTravelTimeInformation(travelTimeMatrixInput);
        initializeOriginDestination(originLocation, destinationLocation);
        addNodesToGraph(tasks);
        sourceId = getNewNodeId();
        sinkId = getNewNodeId();
    }

    private void initializeOriginDestination(ILocation originLocation, ILocation destinationLocation) {
        initializeOrigin(originLocation);
        initializeDestination(destinationLocation);
        nodes.add(origin);
        nodes.add(destination);
    }

    /**
     * Initialize the origin in the graph. If null the origin will be the first task in the route. This is represented
     * by a locationId = -1.
     *
     * @param originLocation Location of the origin, must be in the travel matrix, or null.
     */
    private void initializeOrigin(ILocation originLocation) {
        if (originLocation != null) {
            this.origin = new Node(getNewNodeId(), null, getLocationId(originLocation));
            locationToLocationIds.put(originLocation, origin.getLocationId());
        } else {
            this.origin = new Node(getNewNodeId(), null, -1);
        }
    }

    /**
     * Initialize the destination in the graph. If null the destination will be the first task in the route. This is
     * represented by a locationId = -1.
     *
     * @param destinationLocation Location of the destination, must be in the travel matrix, or null.
     */
    private void initializeDestination(ILocation destinationLocation) {
        if (destinationLocation != null) {
            this.destination = new Node(getNewNodeId(), null, getLocationId(destinationLocation));
            locationToLocationIds.put(destinationLocation, destination.getLocationId());
        } else {
            this.destination = new Node(getNewNodeId(), null, -1);
        }
    }

    private void addNodesToGraph(Collection<? extends ITask> tasks) {
        for (ITask task : tasks) {
            int locationId = getLocationId(task.getLocation());
            Node node = new Node(getNewNodeId(), task, locationId);
            nodes.add(node);
            taskToNodes.put(task, node);
        }
    }

    private void updateTravelTimeInformation(ITravelTimeMatrix travelTimeMatrixInput) {
        int n = createLocations(travelTimeMatrixInput);
        this.travelTimeMatrix = new Integer[n][n];
        for (ILocation locationA : travelTimeMatrixInput.getLocations()) {
            for (ILocation locationB : travelTimeMatrixInput.getLocations()) {
                if (locationA != locationB) {
                    addTravelTime(travelTimeMatrixInput, locationA, locationB);
                }
            }
        }
    }

    /**
     * Create all location ids.
     *
     * @param travelTimeMatrixInput Travel matrix to get locations from.
     * @return Number of locations
     */
    private int createLocations(ITravelTimeMatrix travelTimeMatrixInput) {
        for (ILocation location : travelTimeMatrixInput.getLocations()) {
            int locationId = getNewLocationId();
            locationToLocationIds.put(location, locationId);
        }
        return travelTimeMatrixInput.getLocations().size();
    }

    public int getLocationId(ILocation location) {
        return locationToLocationIds.get(location);
    }

    private void addTravelTime(ITravelTimeMatrix travelTimeMatrixInput, ILocation fromLocation, ILocation toLocation) {
        int fromId = getLocationId(fromLocation);
        int toId = getLocationId(toLocation);

        if (!travelTimeMatrixInput.connected(fromLocation, toLocation))
            return;
        int travelTime = travelTimeMatrixInput.getTravelTime(fromLocation, toLocation);
        this.travelTimeMatrix[fromId][toId] = travelTime;
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


    public void useOpenStartRoutes() {
        if (!(origin instanceof VirtualNode))
            origin = new VirtualNode(sourceId);
    }

    /**
     * Open ended routes ensures that the route ends at the last task in the route. Hence the route cannot have a
     * destination.
     * The destination of a route is overwritten when this is set. In the same way when the destination is updated the
     * route is no longer considered to be open ended.
     */
    public void useOpenEndedRoutes() {
        if (!(destination instanceof VirtualNode))
            destination = new VirtualNode(sinkId);
    }
}
