package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveFunctionsIntraRouteHandler {

    private Map<String, WeightObjectivePair> objectiveFunctions;

    public ObjectiveFunctionsIntraRouteHandler() {
        objectiveFunctions = new HashMap<>();
    }

    public void addIntraShiftObjectiveFunction(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.put(objectiveFunctionId, new WeightObjectivePair(weight, objectiveIntraShift));
    }

    public WeightedObjective calculateObjectiveValue(WeightedObjective objective, long travelTime, ITask task, long startOfServiceNextTask,
                                                     long visitEnd, long syncedTaskLatestStartTime, long endOfShift) {
        WeightedObjective newObjective = new WeightedObjective(objective);

        ObjectiveInfo objectiveInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);

        for (Map.Entry<String, WeightObjectivePair> kvp : objectiveFunctions.entrySet()) {
            WeightObjectivePair objectivePair = kvp.getValue();
            newObjective.updateObjective(kvp.getKey(), objectivePair.getWeight(), objectivePair.calcObjectiveValue(objectiveInfo));
        }
        return newObjective;
    }

    private static class WeightObjectivePair {

        private final double weight;
        private final IObjectiveFunctionIntraRoute objectiveFunction;

        public WeightObjectivePair(double weight, IObjectiveFunctionIntraRoute objectiveFunction) {
            this.weight = weight;
            this.objectiveFunction = objectiveFunction;
        }

        public double getWeight() {
            return weight;
        }

        public double calcObjectiveValue(ObjectiveInfo objectiveInfo) {
            return objectiveFunction.calculateIncrementalObjectiveValueFor(objectiveInfo);
        }

    }
}
