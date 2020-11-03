package com.visma.of.rp.routeevaluator.intraRouteEvaluation.objectives;

import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchInfo;

/**
 * Calculates objective values and check whether one objective dominates the other.
 */
public class Objective {
    private double objectiveValue;

    public Objective(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public Objective extend(SearchInfo searchInfo, Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime) {
        if (toNode.getTask() == null) {
            return createObjectiveFunctionToOffice(searchInfo, travelTime, startOfServiceNextTask);
        } else {
            return createObjectiveFunctionFor(searchInfo, toNode, travelTime, startOfServiceNextTask, syncedTaskLatestStartTime);
        }
    }

    private Objective createObjectiveFunctionFor(SearchInfo searchInfo, Node toNode, long travelTimeWithParking, long arrivalTime, long syncedTaskLatestStartTime) {
        double newObjectiveValue = searchInfo.calculateObjectiveValue(travelTimeWithParking, toNode.getTask(), arrivalTime, syncedTaskLatestStartTime);
        return new Objective(this.objectiveValue + newObjectiveValue);
    }

    private Objective createObjectiveFunctionToOffice(SearchInfo searchInfo, long travelTimeWithParking, long officeArrivalTime) {
        double newObjectiveValue = searchInfo.calculateObjectiveValue(travelTimeWithParking, null, officeArrivalTime, Double.MAX_VALUE);
        return new Objective(this.objectiveValue + newObjectiveValue);
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public int dominates(Objective other) {
        if (this.objectiveValue < other.objectiveValue)
            return -1;
        else if (this.objectiveValue > other.objectiveValue)
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return Double.toString(objectiveValue);
    }
}
