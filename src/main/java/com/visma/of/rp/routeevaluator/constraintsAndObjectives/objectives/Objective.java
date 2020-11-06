package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;

/**
 * Calculates objective values and check whether one objective dominates the other.
 */
public class Objective {

    private double objectiveValue;

    public Objective(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public Objective extend(Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime, long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        if (toNode.getTask() == null) {
            return createObjectiveFunctionToOffice(travelTime, startOfServiceNextTask, endOfShift, objectiveFunctionsIntraRouteHandler);
        } else {
            return createObjectiveFunctionFor(toNode, travelTime, startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift, objectiveFunctionsIntraRouteHandler);
        }
    }

    public double calculateObjectiveValue(long travelTime, ITask task, long startOfServiceNextTask,
                                          long syncedTaskLatestStartTime, long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        ObjectiveInfo costInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);
        return objectiveFunctionsIntraRouteHandler.calculateIncrementalObjectiveValue(costInfo);
    }

    private Objective createObjectiveFunctionFor( Node toNode, long travelTimeWithParking, long startOfServiceNextTask, long syncedTaskLatestStartTime, long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        double newObjectiveValue = calculateObjectiveValue(travelTimeWithParking, toNode.getTask(), startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift, objectiveFunctionsIntraRouteHandler);
        return new Objective(this.objectiveValue + newObjectiveValue);
    }

    private Objective createObjectiveFunctionToOffice( long travelTimeWithParking, long officeArrivalTime, long endOfShift, ObjectiveFunctionsIntraRouteHandler objectiveFunctionsIntraRouteHandler) {
        double newObjectiveValue = calculateObjectiveValue(travelTimeWithParking, null, officeArrivalTime, Long.MAX_VALUE, endOfShift, objectiveFunctionsIntraRouteHandler);
        return new Objective(this.objectiveValue + newObjectiveValue);
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public int dominates(Objective other) {
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
