package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.costFunctions.IncrementalCostHandler;
import com.visma.of.rp.routeevaluator.costFunctions.IncrementalCostInfo;
import com.visma.of.rp.routeevaluator.hardConstraints.HardConstraintsIncremental;
import com.visma.of.rp.routeevaluator.searchGraph.Node;
import com.visma.of.rp.routeevaluator.searchGraph.SearchGraph;


public class SearchInfo {
    private SearchGraph graph;
    private IncrementalCostHandler incrementalCostHandler;
    private HardConstraintsIncremental hardConstraintsEvaluator;
    private long[] syncedNodesStartTime;
    private long[] syncedNodesLatestStartTime;
    private long endOfShift;
    private IShift employeeWorkShift;

    public SearchInfo(SearchGraph graph) {
        this.graph = graph;
        this.incrementalCostHandler = new IncrementalCostHandler();
        this.hardConstraintsEvaluator = new HardConstraintsIncremental();
    }

    protected void replaceHardConstraintsEvaluator(HardConstraintsIncremental hardConstraintsEvaluator) {
        this.hardConstraintsEvaluator = hardConstraintsEvaluator;
    }

    public boolean isFeasible(long earliestPossibleReturnToOfficeTime, ITask task, long serviceStartTime, Long syncedStartTime) {
        return hardConstraintsEvaluator.isFeasible(endOfShift, earliestPossibleReturnToOfficeTime, task, serviceStartTime, syncedStartTime);
    }

    public long getTravelTimeToOffice(Node toNode) {
        if (toNode == graph.getOffice())
            return 0;
        return graph.getEdgesNodeToNode()
                .getEdge(toNode, graph.getOffice())
                .getTravelInfo()
                .getTravelTimeWithParking();
    }

    public void update(long[] syncedNodesStartTime, long[] syncedNodesLatestStartTime, long endOfShift, IShift employeeWorkShift) {
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.syncedNodesLatestStartTime = syncedNodesLatestStartTime;
        this.endOfShift = endOfShift;
        this.employeeWorkShift = employeeWorkShift;
    }

    public double calculateCost(double travelTime, ITask task,
                                double arrivalTime, double syncedLatestStartTime) {
        double visitEnd = task != null ? arrivalTime + task.getDurationSeconds() : 0;
        IncrementalCostInfo costInfo = new IncrementalCostInfo(travelTime, task, visitEnd, arrivalTime, syncedLatestStartTime, endOfShift);
        return incrementalCostHandler.calculateIncrementalCost(costInfo);
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

}
