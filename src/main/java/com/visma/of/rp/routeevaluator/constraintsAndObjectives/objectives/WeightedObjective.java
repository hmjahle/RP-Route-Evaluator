package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Node;

/**
 * Calculates objective values and check whether one objective dominates the other.
 */
public class WeightedObjective  {

    private double objectiveValue;

    public WeightedObjective(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public WeightedObjective extend(Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime,
                                    long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        double newObjectiveValue = calculateObjectiveValue(travelTime, toNode.getTask(),
                startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift, objectiveFunctionsIntraRouteHandler);
        return new WeightedObjective(this.objectiveValue + newObjectiveValue);
    }

    public double calculateObjectiveValue(long travelTime, ITask task, long startOfServiceNextTask,
                                          long syncedTaskLatestStartTime, long endOfShift,
                                          ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        ObjectiveInfo costInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);
        return objectiveFunctionsIntraRouteHandler.calculateIncrementalObjectiveValue(costInfo);
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
