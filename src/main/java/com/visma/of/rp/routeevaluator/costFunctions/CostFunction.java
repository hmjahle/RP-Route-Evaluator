package com.visma.of.rp.routeevaluator.costFunctions;

import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchInfo;

//Calculates fitness values and test whether one costFunction dominates the other.
public class CostFunction {
    private double cost;

    public CostFunction(double cost) {
        this.cost = cost;
    }

    public CostFunction extend(SearchInfo searchInfo, Node toNode, long travelTimeWithParking, long arrivalTime,
                               long officeArrivalTime,
                               long syncedTaskLatestStartTime) {
        if (toNode.getTask() == null) {
            return createCostFunctionToOffice(searchInfo, travelTimeWithParking, officeArrivalTime);
        } else {
            return createCostFunctionFor(searchInfo, toNode, travelTimeWithParking, arrivalTime, syncedTaskLatestStartTime);
        }
    }

    private CostFunction createCostFunctionFor(SearchInfo searchInfo, Node toNode, long travelTimeWithParking, long arrivalTime, long syncedTaskLatestStartTime) {
        double newFitness = searchInfo.calculateCost(travelTimeWithParking, toNode.getTask(), arrivalTime, syncedTaskLatestStartTime);
        return new CostFunction(this.cost + newFitness);
    }

    private CostFunction createCostFunctionToOffice(SearchInfo searchInfo, long travelTimeWithParking, long officeArrivalTime) {
        double newFitness = searchInfo.calculateCost(travelTimeWithParking, null, officeArrivalTime, Double.MAX_VALUE);
        return new CostFunction(this.cost + newFitness);
    }

    public double getCost() {
        return cost;
    }

    public int dominates(CostFunction cost) {
        if (this.cost < cost.cost)
            return -1;
        else if (this.cost > cost.cost)
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return Double.toString(cost);
    }
}
