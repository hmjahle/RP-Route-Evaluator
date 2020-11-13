package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Node;

/**
 * Calculates objective values and check whether one objective dominates the other.
 */
public class WeightedObjective {

    private double objectiveValue;

    public WeightedObjective(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public WeightedObjective(WeightedObjective other) {
        this.objectiveValue = other.objectiveValue;
    }

    public WeightedObjective extend(Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime,
                                    long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        ITask task = toNode.getTask();
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        return objectiveFunctionsIntraRouteHandler.calculateObjectiveValue(this, travelTime, task,
                startOfServiceNextTask, visitEnd, syncedTaskLatestStartTime, endOfShift);
    }

    public void updateObjective(String objectiveFunctionId, double weight, double objectiveValue) {
        this.objectiveValue += objectiveValue * weight;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public int dominates(WeightedObjective other) {
        if (this.objectiveValue < other.objectiveValue) {
            return -1;
        } else if (this.objectiveValue > other.objectiveValue)
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return Double.toString(objectiveValue);
    }
}
