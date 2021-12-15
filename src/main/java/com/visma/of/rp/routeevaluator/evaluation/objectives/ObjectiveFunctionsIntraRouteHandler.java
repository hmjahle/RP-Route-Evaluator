package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.evaluation.info.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.algorithm.IObjective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectiveFunctionsIntraRouteHandler {

    private Map<String, WeightObjectivePair> objectiveFunctions;

    public ObjectiveFunctionsIntraRouteHandler() {
        objectiveFunctions = new HashMap<>();
    }

    public void addIntraShiftObjectiveFunction(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.put(objectiveFunctionId, new WeightObjectivePair(weight, objectiveIntraShift));
    }

    public boolean removeObjective(String name) {
        return objectiveFunctions.remove(name) != null;
    }

    public IObjective calculateObjectiveValue(IObjective currentObjective, long travelTime, ITask task, long startOfServiceNextTask,
                                              long visitEnd, long syncedTaskLatestStartTime, long endOfShift) {

        IObjective newObjective = currentObjective.initializeNewObjective();
        ObjectiveInfo objectiveInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, endOfShift);

        for (Map.Entry<String, WeightObjectivePair> kvp : objectiveFunctions.entrySet()) {
            WeightObjectivePair objectivePair = kvp.getValue();
            newObjective.incrementObjective(kvp.getKey(), objectivePair.getWeight(), objectivePair.calcObjectiveValue(objectiveInfo));
        }
        return newObjective;
    }

    private static class WeightObjectivePair {

        private final double weight;
        private final IObjectiveFunctionIntraRoute objectiveFunction;

        private WeightObjectivePair(double weight, IObjectiveFunctionIntraRoute objectiveFunction) {
            this.weight = weight;
            this.objectiveFunction = objectiveFunction;
        }

        private double getWeight() {
            return weight;
        }

        private double calcObjectiveValue(ObjectiveInfo objectiveInfo) {
            return objectiveFunction.calculateIncrementalObjectiveValueFor(objectiveInfo);
        }
    }

    public List<IObjectiveFunctionIntraRoute> extractIObjectiveFunctionIntraRoute() {
        List<IObjectiveFunctionIntraRoute> objectiveFunctionIntraRoutes = new ArrayList<>();
        for (WeightObjectivePair weightObjectivePair : objectiveFunctions.values())
            objectiveFunctionIntraRoutes.add(weightObjectivePair.objectiveFunction);
        return objectiveFunctionIntraRoutes;
    }
}
