package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.solvers.fitness.FitnessIncremental;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.hardConstraints.HardConstraintsIncremental;

public class SearchInfo {
    private SearchGraph graph;
    private FitnessIncremental fitnessCalculator;
    private HardConstraintsIncremental hardConstraintsEvaluator;
    private long[] syncedNodesStartTime;
    private long[] syncedNodesLatestStartTime;
    private long endOfShift;
    private EmployeeWorkShift employeeWorkShift;

    public SearchInfo(SearchGraph graph) {
        this.graph = graph;
        this.fitnessCalculator = new FitnessIncremental(graph.getProblemInstance());
        this.hardConstraintsEvaluator = new HardConstraintsIncremental(graph.getProblemInstance());
    }

    protected void replaceHardConstraintsEvaluator(HardConstraintsIncremental hardConstraintsEvaluator) {
        this.hardConstraintsEvaluator = hardConstraintsEvaluator;
    }

    public boolean isFeasible(long earliestPossibleReturnToOfficeTime, Task task, long serviceStartTime, Long syncedStartTime) {
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

    public void update(long[] syncedNodesStartTime, long[] syncedNodesLatestStartTime, long endOfShift, EmployeeWorkShift employeeWorkShift) {
        this.syncedNodesStartTime = syncedNodesStartTime;
        this.syncedNodesLatestStartTime = syncedNodesLatestStartTime;
        this.endOfShift = endOfShift;
        this.employeeWorkShift = employeeWorkShift;
    }

    double calculateFitness(double travelTime, Task task,
                            double arrivalTime, double syncedLatestStartTime) {
        double visitEnd = task != null ? arrivalTime + task.getDurationSeconds() : 0;
        MarginalFitnessInfo fitnessInfo = new MarginalFitnessInfo(travelTime, task, visitEnd, arrivalTime, syncedLatestStartTime, endOfShift);
        return fitnessCalculator.calculateIncrementalFitness(fitnessInfo);
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

    public EmployeeWorkShift getEmployeeWorkShift() {
        return employeeWorkShift;
    }

    public long getEndOfShift() {
        return endOfShift;
    }

}
