package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Node;

/**
 * Calculates objective values and check whether one objective dominates the other.
 */
public abstract class ObjectiveAbstract {

    protected double objectiveValue;

    protected ObjectiveAbstract(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public ObjectiveAbstract extend(Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime,
                                    long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        ITask task = toNode.getTask();
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        return objectiveFunctionsIntraRouteHandler.calculateObjectiveValue(this, travelTime, task,
                startOfServiceNextTask, visitEnd, syncedTaskLatestStartTime, endOfShift);
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    protected abstract void updateObjective(String objectiveFunctionId, double weight, double objectiveValue);

    protected abstract ObjectiveAbstract initializeNewObjective();

    public abstract int dominates(ObjectiveAbstract other);

    @Override
    public String toString() {
        return Double.toString(objectiveValue);
    }
}
