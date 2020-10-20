package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;


import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.task.Task;
import probleminstance.entities.transport.Transport;
import routeplanner.solvers.fitness.entities.TravelInfo;

import java.util.*;

public class SearchGraph {

    private ProblemInstance problemInstance;
    private List<Node> nodes;
    private Map<Task, Node> nodesToTask;
    private Node office;
    private Set<Edge> edgesAll;
    private DistanceSet edgesNodeToNode;
    private Map<Edge, Map<Transport, TravelInfo>> edgeTravelInfo;
    private int nodeId;
    private int edgeId;
    private Transport currentTransportMode;

    SearchGraph(ProblemInstance problemInstance) {
        this.nodes = new ArrayList<>();
        this.edgesAll = new HashSet<>();
        this.nodesToTask = new HashMap<>();
        this.problemInstance = problemInstance;
        this.edgeTravelInfo = new HashMap<>();
        this.nodeId = 0;
        this.edgeId = 0;
        this.populateGraph(problemInstance);
    }

    public ProblemInstance getProblemInstance() {
        return problemInstance;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public DistanceSet getEdgesNodeToNode() {
        return edgesNodeToNode;
    }

    private int getNewNodeId() {
        return nodeId++;
    }

    private int getNewEdgeId() {
        return edgeId++;
    }

    private void populateGraph(ProblemInstance problemInstance) {
        addNodesToGraph(problemInstance);
        addTravelInfo();
        addEdges();
    }

    public void setEdgesTransportMode(Transport transportMode) {
        if (transportMode != currentTransportMode) {
            currentTransportMode = transportMode;
            for (Edge edge : edgeTravelInfo.keySet()) {
                edge.setTravelInfo(edgeTravelInfo.get(edge).get(currentTransportMode));
            }
        }
    }

    private void addEdges() {
        edgesNodeToNode = new DistanceSet(nodes.size());
        edgesNodeToNode.update(edgesAll);
    }

    private void addTravelInfo() {
        for (Node node1 : nodes) {
            for (Node node2 : nodes) {
                if (node1 != node2) {
                    addEdge(Transport.values(), node1, node2);
                }
            }
        }
    }

    private void addNodesToGraph(ProblemInstance problemInstance) {
        office = new Node(getNewNodeId(), null, problemInstance.getOfficeInfo(), problemInstance);
        nodes.add(office);
        for (Task task : problemInstance.getProblemInstanceTaskStructure().getAllTasks()) {
            Node node = new Node(getNewNodeId(), task, task.getAddressEntity(), problemInstance);
            nodes.add(node);
            nodesToTask.put(task, node);
        }
    }

    private void addEdge(Transport[] transportModes, Node node1, Node node2) {
        HashMap<Transport, TravelInfo> eligibleTransportModes = new HashMap<>();
        for (Transport transportMode : transportModes) {
            if (canConnectNodes(transportMode, node1, node2)) {
                TravelInfo travelInfo = getTravelInfo(transportMode, node1, node2);
                if (!node1.getRequirePhysicalAppearance() || !node2.getRequirePhysicalAppearance()) {
                    travelInfo = new TravelInfo(transportMode, 0, 0);
                }
                eligibleTransportModes.put(transportMode, travelInfo);
            }
        }
        Edge edge = new Edge(getNewEdgeId(), node1, node2);
        edgeTravelInfo.put(edge, eligibleTransportModes);
        edgesAll.add(edge);

    }

    private boolean canConnectNodes(Transport transport, Node from, Node to) {
        return problemInstance.getProblemInstanceHelpStructure().getTransportInformation().transportIsPossible(transport,
                from.getAddress(), to.getAddress());
    }

    private TravelInfo getTravelInfo(Transport transport, Node from, Node to) {
        if (!canConnectNodes(transport, from, to)) {
            return null;
        }
        return new TravelInfo(transport,
                problemInstance.getProblemInstanceHelpStructure().getTransportInformation().
                        getTravelTimeWithParkingFor(transport, from.getAddress(), to.getAddress()),
                problemInstance.getProblemInstanceHelpStructure().getTransportInformation().
                        getRawTravelTimeFor(transport, from.getAddress(), to.getAddress()));
    }

    void updateNodeType(List<Task> tasks) {
        for (Task task : tasks) {
            updateNodeType(task);
        }
    }

    void updateNodeType(Task task) {
        nodesToTask.get(task).setSynced(task.isSynced());
    }


    Node getOffice() {
        return office;
    }

    Node getNode(Task task) {
        return nodesToTask.get(task);
    }

}
