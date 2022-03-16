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

    private Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> activeObjectiveFunctions;
    private Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> inactiveObjectiveFunctions;

    public ObjectiveFunctionsIntraRouteHandler() {
        activeObjectiveFunctions = new HashMap<>();
        inactiveObjectiveFunctions = new HashMap<>();
    }

    public ObjectiveFunctionsIntraRouteHandler(ObjectiveFunctionsIntraRouteHandler other) {
        this.activeObjectiveFunctions = new HashMap<>();
        for (Map.Entry<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> kvp : other.activeObjectiveFunctions.entrySet())
            this.activeObjectiveFunctions.put(kvp.getKey(), kvp.getValue());
        this.inactiveObjectiveFunctions = new HashMap<>();
        for (Map.Entry<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> kvp : other.inactiveObjectiveFunctions.entrySet())
            this.inactiveObjectiveFunctions.put(kvp.getKey(), kvp.getValue());
    }

    public void update(ObjectiveFunctionsIntraRouteHandler other) {
        alignInactiveOrActiveObjectives(other.activeObjectiveFunctions, this.inactiveObjectiveFunctions);
        alignInactiveOrActiveObjectives(other.inactiveObjectiveFunctions, this.activeObjectiveFunctions);
    }

    private void alignInactiveOrActiveObjectives(Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> activeObjectiveFunctions, Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> inactiveObjectiveFunctions) {
        for (String name : activeObjectiveFunctions.keySet()) {
            if (inactiveObjectiveFunctions.containsKey(name)) {
                WeightObjectivePair<IObjectiveFunctionIntraRoute> objectivePair = inactiveObjectiveFunctions.remove(name);
                activeObjectiveFunctions.put(name, objectivePair);
            }
        }
    }

    public void addIntraShiftObjectiveFunction(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        activeObjectiveFunctions.put(objectiveFunctionId, new WeightObjectivePair<>(weight, objectiveIntraShift));
    }

    public boolean removeObjective(String name) {
        if (inactiveObjectiveFunctions.remove(name) != null)
            return true;
        return activeObjectiveFunctions.remove(name) != null;

    }

    public void updateObjectiveWeight(String name, double newWeight) {
        WeightObjectivePair<IObjectiveFunctionIntraRoute> obj = activeObjectiveFunctions.getOrDefault(name, inactiveObjectiveFunctions.get(name));
        obj.setWeight(newWeight);
    }

    public IRouteEvaluatorObjective calculateObjectiveValue(IRouteEvaluatorObjective currentObjective, long travelTime, ITask task, long startOfServiceNextTask,
                                                            long visitEnd, long syncedTaskLatestStartTime, IShift employeeWorkShift) {

        IRouteEvaluatorObjective newObjective = currentObjective.initializeNewObjective();
        ObjectiveInfo objectiveInfo = new ObjectiveInfo(travelTime, task, visitEnd, startOfServiceNextTask,
                syncedTaskLatestStartTime, employeeWorkShift);

        for (Map.Entry<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> objectivePair : activeObjectiveFunctions.entrySet()) {
            newObjective.incrementObjective(objectivePair.getKey(), objectivePair.getValue().getWeight(),
                    objectivePair.getValue().getObjectiveFunction().calculateIncrementalObjectiveValueFor(objectiveInfo));
        }
        return newObjective;
    }

    public List<IObjectiveFunctionIntraRoute> extractIObjectiveFunctionIntraRoute() {
        List<IObjectiveFunctionIntraRoute> objectiveFunctionIntraRoutes = new ArrayList<>();
        for (WeightObjectivePair<IObjectiveFunctionIntraRoute> weightObjectivePair : activeObjectiveFunctions.values())
            objectiveFunctionIntraRoutes.add(weightObjectivePair.getObjectiveFunction());
        return objectiveFunctionIntraRoutes;
    }

    public Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> getActiveObjectiveFunctions() {
        return activeObjectiveFunctions;
    }

    public Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> getInactiveObjectiveFunctions() {
        return inactiveObjectiveFunctions;
    }
}
