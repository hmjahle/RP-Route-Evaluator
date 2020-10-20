package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

//Calculates fitness values and test whether one costFunction dominates the other.
public class CostFunction {
    private double fitness;

    public CostFunction(double fitness) {
        this.fitness = fitness;
    }

    CostFunction extend(SearchInfo searchInfo, Node toNode, long travelTimeWithParking, long arrivalTime,
                        long officeArrivalTime,
                        long syncedTaskLatestStartTime) {
        if (toNode.getTask() == null) {
            return createCostFunctionToOffice(searchInfo, travelTimeWithParking, officeArrivalTime);
        } else {
            return createCostFunctionFor(searchInfo, toNode, travelTimeWithParking, arrivalTime, syncedTaskLatestStartTime);
        }
    }

    private CostFunction createCostFunctionFor(SearchInfo searchInfo, Node toNode, long travelTimeWithParking, long arrivalTime, long syncedTaskLatestStartTime) {
        double newFitness = searchInfo.calculateFitness(travelTimeWithParking, toNode.getTask(), arrivalTime, syncedTaskLatestStartTime);
        return new CostFunction(this.fitness + newFitness);
    }

    private CostFunction createCostFunctionToOffice(SearchInfo searchInfo, long travelTimeWithParking, long officeArrivalTime) {
        double newFitness = searchInfo.calculateFitness(travelTimeWithParking, null, officeArrivalTime, Double.MAX_VALUE);
        return new CostFunction(this.fitness + newFitness);
    }

    public double getFitness() {
        return fitness;
    }

    public int dominates(CostFunction cost) {
        if (this.fitness < cost.fitness)
            return -1;
        else if (this.fitness > cost.fitness)
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return Double.toString(fitness);
    }
}
