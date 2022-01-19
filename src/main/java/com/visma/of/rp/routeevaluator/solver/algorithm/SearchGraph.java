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
        if (originLocation == null) {
            useOpenStartRoutes();
        } else if (origin instanceof VirtualNode) {
            this.origin = new Node(sourceId, null, getLocationId(originLocation));
        } else
            origin.setLocationId(locationToLocationIds.get(originLocation));
    }

    /**
     * Location must be present in the route evaluator, i.e.,
     * the travel times matrix given when the route evaluator was constructed.
     *
     * @param destinationLocation The the location where the route should end.
     */
    public void updateDestination(ILocation destinationLocation) {
        if (destinationLocation == null) {
            useOpenEndedRoutes();
        } else if ((destination instanceof VirtualNode)) {
            this.destination = new Node(sinkId, null, getLocationId(destinationLocation));
        } else
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

    private void populateGraph(ITravelTimeMatrix travelTimeMatrixInput, Collection<? extends
            ITask> tasks, ILocation originLocation,
                               ILocation destinationLocation) {
        updateTravelTimeInformation(travelTimeMatrixInput);
        initializeOriginDestination(originLocation, destinationLocation);
        addNodesToGraph(tasks);
    }

    private void initializeOriginDestination(ILocation originLocation, ILocation destinationLocation) {
        sourceId = getNewNodeId();
        sinkId = getNewNodeId();
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
            this.origin = new Node(sourceId, null, getLocationId(originLocation));
            locationToLocationIds.put(originLocation, origin.getLocationId());
        } else {
            this.origin = new VirtualNode(sourceId);
        }
    }

    /**
     * Initialize the destination in the graph. If null the destination will be the last task in the route. This is
     * represented by a locationId = -1.
     *
     * @param destinationLocation Location of the destination, must be in the travel matrix, or null.
     */
    private void initializeDestination(ILocation destinationLocation) {
        if (destinationLocation != null) {
            this.destination = new Node(sinkId, null, getLocationId(destinationLocation));
            locationToLocationIds.put(destinationLocation, destination.getLocationId());
        } else {
            this.destination = new VirtualNode(sinkId);

        }
    }

    private void addNodesToGraph(Collection<? extends ITask> tasks) {
        for (ITask task : tasks) {
            int locationId = task.getLocation() == null ? -1 : getLocationId(task.getLocation());
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
                addTravelTime(travelTimeMatrixInput, locationA, locationB);
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

    /**
     * Gets the location id of a location in the graph, the graph must contain the location.
     *
     * @param location Location to find id for
     * @return integer location id.
     */
    public int getLocationId(ILocation location) {
        return locationToLocationIds.get(location);
    }

    /**
     * Gets the location id of a task in the graph, the graph must contain the task.
     *
     * @param task Task to find location id for.
     * @return integer location id.
     */
    public int getLocationId(ITask task) {
        return taskToNodes.get(task).getLocationId();
    }

    private void addTravelTime(ITravelTimeMatrix travelTimeMatrixInput, ILocation fromLocation, ILocation
            toLocation) {
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
