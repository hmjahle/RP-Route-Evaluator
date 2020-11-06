package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
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

    public Objective extend(SearchInfo searchInfo, Node toNode, long travelTime, long startOfServiceNextTask, long syncedTaskLatestStartTime, long endOfShift, ObjectivesIntraRouteHandler objectivesIntraRouteHandler) {
        if (toNode.getTask() == null) {
            return createObjectiveFunctionToOffice(searchInfo, travelTime, startOfServiceNextTask, endOfShift, objectivesIntraRouteHandler);
        } else {
            return createObjectiveFunctionFor(searchInfo, toNode, travelTime, startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift, objectivesIntraRouteHandler);
        }
    }

    public double calculateObjectiveValue(long travelTime, ITask task, long startOfServiceNextTask,
                                          long syncedTaskLatestStartTime, long endOfShift, ObjectivesIntraRouteHandler objectivesIntraRouteHandler) {
        long visitEnd = task != null ? startOfServiceNextTask + task.getDuration() : 0;
        ObjectiveInfo costInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);
        return objectivesIntraRouteHandler.calculateIncrementalObjectiveValue(costInfo);
    }

    private Objective createObjectiveFunctionFor(SearchInfo searchInfo, Node toNode, long travelTimeWithParking, long startOfServiceNextTask, long syncedTaskLatestStartTime, long endOfShift, ObjectivesIntraRouteHandler objectivesIntraRouteHandler) {
        double newObjectiveValue = calculateObjectiveValue(travelTimeWithParking, toNode.getTask(), startOfServiceNextTask, syncedTaskLatestStartTime, endOfShift, objectivesIntraRouteHandler);
        return new Objective(this.objectiveValue + newObjectiveValue);
    }

    private Objective createObjectiveFunctionToOffice(SearchInfo searchInfo, long travelTimeWithParking, long officeArrivalTime, long endOfShift, ObjectivesIntraRouteHandler objectivesIntraRouteHandler) {
        double newObjectiveValue = calculateObjectiveValue(travelTimeWithParking, null, officeArrivalTime, Long.MAX_VALUE, endOfShift, objectivesIntraRouteHandler);
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
