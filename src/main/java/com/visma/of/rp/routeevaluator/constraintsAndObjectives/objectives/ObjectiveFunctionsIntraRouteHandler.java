package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IObjectiveFunctionIntraRoute;

import java.util.HashMap;


public class ObjectiveFunctionsIntraRouteHandler {


    private HashMap<String, WeightObjectivePair> objectiveFunctions;

    public ObjectiveFunctionsIntraRouteHandler() {
        objectiveFunctions = new HashMap<>();
    }

    public void addIntraShiftObjectiveFunction(String objectiveFunctionId, double weight, IObjectiveFunctionIntraRoute objectiveIntraShift) {
        objectiveFunctions.put(objectiveFunctionId, new WeightObjectivePair(weight, objectiveIntraShift));
    }

    public double calculateIncrementalObjectiveValue(ObjectiveInfo objectiveInfo) {
        double objectiveValue = 0.0;
        for (WeightObjectivePair objective : objectiveFunctions.values())
            objectiveValue += objective.weight * objective.objectiveFunction.calculateIncrementalObjectiveValueFor(objectiveInfo);
        return objectiveValue;
    }

    private class WeightObjectivePair {
        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public IObjectiveFunctionIntraRoute getObjectiveFunction() {
            return objectiveFunction;
        }

        public void setObjectiveFunction(IObjectiveFunctionIntraRoute objectiveFunction) {
            this.objectiveFunction = objectiveFunction;
        }

        Double weight;
        IObjectiveFunctionIntraRoute objectiveFunction;

        public WeightObjectivePair(Double weight, IObjectiveFunctionIntraRoute objectiveFunction) {
            this.weight = weight;
            this.objectiveFunction = objectiveFunction;
        }
    }
}
