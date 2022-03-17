package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.evaluation.info.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.algorithm.IRouteEvaluatorObjective;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveFunctionsIntraRouteHandler {

    private final Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> activeObjectiveFunctions;
    private final Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> inactiveObjectiveFunctions;

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

    /**
     * Objective functions that are active in other is set to active in this and inactive in other are set inactive in this.
     *
     * @param other Other objective to adapt to.
     */
    public void update(ObjectiveFunctionsIntraRouteHandler other) {
        for (String name : other.activeObjectiveFunctions.keySet()) {
            if (this.inactiveObjectiveFunctions.containsKey(name)) {
                WeightObjectivePair<IObjectiveFunctionIntraRoute> objectivePair = this.inactiveObjectiveFunctions.remove(name);
                this.activeObjectiveFunctions.put(name, objectivePair);
            }
        }
        for (String name : other.inactiveObjectiveFunctions.keySet()) {
            if (this.activeObjectiveFunctions.containsKey(name)) {
                WeightObjectivePair<IObjectiveFunctionIntraRoute> objectivePair = this.activeObjectiveFunctions.remove(name);
                this.inactiveObjectiveFunctions.put(name, objectivePair);
            }
        }
    }

    /**
     * Activates an inactive Objective
     *
     * @param name Name to be activated.
     * @return True if variable was activated, otherwise false.
     */
    public boolean activateObjective(String name) {
        WeightObjectivePair<IObjectiveFunctionIntraRoute> constraintToActivate = inactiveObjectiveFunctions.remove(name);
        if (constraintToActivate == null)
            return false;
        activeObjectiveFunctions.put(name, constraintToActivate);
        return true;
    }

    /**
     * Deactivates an active Objective
     *
     * @param name Name to be deactivated.
     * @return True if variable was deactivated, otherwise false.
     */
    public boolean deactivateObjective(String name) {
        WeightObjectivePair<IObjectiveFunctionIntraRoute> constraintToActivate = activeObjectiveFunctions.remove(name);
        if (constraintToActivate == null)
            return false;
        inactiveObjectiveFunctions.put(name, constraintToActivate);
        return true;
    }


    public void addIntraShiftObjectiveFunction(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        activeObjectiveFunctions.put(objectiveFunctionId, new WeightObjectivePair<>(weight, objectiveIntraShift));
    }

    public boolean removeObjective(String name) {
        if (inactiveObjectiveFunctions.remove(name) != null)
            return true;
        return activeObjectiveFunctions.remove(name) != null;

    }

    public WeightObjectivePair<IObjectiveFunctionIntraRoute> getWeightObjectivePair(String name) {
        return activeObjectiveFunctions.getOrDefault(name, inactiveObjectiveFunctions.get(name));

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


    public Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> getActiveObjectiveFunctions() {
        return activeObjectiveFunctions;
    }

    public Map<String, WeightObjectivePair<IObjectiveFunctionIntraRoute>> getInactiveObjectiveFunctions() {
        return inactiveObjectiveFunctions;
    }
}
