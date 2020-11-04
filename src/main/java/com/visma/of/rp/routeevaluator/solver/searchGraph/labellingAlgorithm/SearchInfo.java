package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.objectives.ObjectivesIntraRouteHandler;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Edge;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;


public class SearchInfo {
    private SearchGraph graph;
    public ObjectivesIntraRouteHandler objectives;
    private ConstraintsIntraRouteHandler constraints;
    private long[] syncedNodesStartTime;
    private long[] syncedNodesLatestStartTime;
    private long endOfShift;
    private IShift employeeWorkShift;
    private long robustness;

    public SearchInfo(SearchGraph graph) {
        this.graph = graph;
        this.robustness = graph.getRobustTimeSeconds();
        this.objectives = new ObjectivesIntraRouteHandler();
        this.constraints = new ConstraintsIntraRouteHandler();
    }

    public void addObjectiveIntraShift(IObjectiveIntraRoute objectiveIntraShift) {
        objectives.addObjectiveIntraShift(objectiveIntraShift);
    }

    public boolean isFeasible(long earliestOfficeReturn, ITask task, long startOfServiceNextTask, long syncedLatestStart) {
        ConstraintInfo constraintInfo = new ConstraintInfo(endOfShift, earliestOfficeReturn, task, startOfServiceNextTask, syncedLatestStart);
        return constraints.isFeasible(constraintInfo);
    }

    /**
     * The travel time back to the office. If the node has the same address as the office 0 is returned.
     * IF the node is not connected to the office 0 is returned!
     *
     * @param node Node from which the travel time to the office is calculated.
     * @return Travel time in seconds.
     */
    public long getTravelTimeToOffice(Node node) {
        if (node.getAddress() == graph.getOffice().getAddress())
            return 0;
        Edge edge = graph.getEdgesNodeToNode()
                .getEdge(node, graph.getOffice());
        return edge == null ? 0 : edge.getTravelTime();
    }

    public void update(long[] syncedNodesStartTime, long[] syncedNodesLatestStartTime, long endOfShift, IShift employeeWorkShift) {
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.syncedNodesLatestStartTime = syncedNodesLatestStartTime;
        this.endOfShift = endOfShift;
        this.employeeWorkShift = employeeWorkShift;
    }

    public double calculateObjectiveValue(long travelTime, ITask task, long startOfServiceNextTask,
                                          long syncedTaskLatestStartTime) {
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        ObjectiveInfo costInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);
        return objectives.calculateIncrementalObjectiveValue(costInfo);
    }

    long[] getSyncedNodesStartTime() {
        return syncedNodesStartTime;
    }

    public long[] getSyncedNodesLatestStartTime() {
        return syncedNodesLatestStartTime;
    }

    public SearchGraph getGraph() {
        return graph;
    }

    public IShift getEmployeeWorkShift() {
        return employeeWorkShift;
    }

    public long getEndOfShift() {
        return endOfShift;
    }

    public long getRobustTimeSeconds() {
        return robustness;
    }
}
