package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.evaluation.info.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.algorithm.IRouteEvaluatorObjective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectiveFunctionsIntraRouteHandler {

    private final Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> objectiveFunctions;

    public ObjectiveFunctionsIntraRouteHandler() {
        objectiveFunctions = new HashMap<>();
    }

    public void addIntraShiftObjectiveFunction(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.put(objectiveFunctionId, new WeightObjectivePair<>(weight, objectiveIntraShift));
    }

    public boolean removeObjective(String name) {
        return objectiveFunctions.remove(name) != null;
    }

    public void updateObjectiveWeight(String name, double newWeight) {
        objectiveFunctions.get(name).setWeight(newWeight);
    }

    public boolean hasObjective(String name) {
        return objectiveFunctions.containsKey(name);
    }

    public IRouteEvaluatorObjective calculateObjectiveValue(IRouteEvaluatorObjective currentObjective, long travelTime, ITask task, long startOfServiceNextTask,
                                                            long visitEnd, long syncedTaskLatestStartTime, IShift employeeWorkShift) {

        IRouteEvaluatorObjective newObjective = currentObjective.initializeNewObjective();
        ObjectiveInfo objectiveInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, employeeWorkShift);

        for (Map.Entry<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> objectivePair : objectiveFunctions.entrySet()) {
            newObjective.incrementObjective(objectivePair.getKey(), objectivePair.getValue().getWeight(),
                    objectivePair.getValue().getObjectiveFunction().calculateIncrementalObjectiveValueFor(objectiveInfo));
        }
        return newObjective;
    }

    public List<IObjectiveFunctionIntraRoute> extractIObjectiveFunctionIntraRoute() {
        List<IObjectiveFunctionIntraRoute> objectiveFunctionIntraRoutes = new ArrayList<>();
        for (WeightObjectivePair<IObjectiveFunctionIntraRoute> weightObjectivePair : objectiveFunctions.values())
            objectiveFunctionIntraRoutes.add(weightObjectivePair.getObjectiveFunction());
        return objectiveFunctionIntraRoutes;
    }
}
