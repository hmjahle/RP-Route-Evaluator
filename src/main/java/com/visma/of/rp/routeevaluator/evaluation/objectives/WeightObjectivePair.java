package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionRoute;

public class WeightObjectivePair<T extends IObjectiveFunctionRoute> {

    private double weight;
    private final T objectiveFunction;

    public WeightObjectivePair(double weight, T objectiveFunction) {
        this.weight = weight;
        this.objectiveFunction = objectiveFunction;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public T getObjectiveFunction() {
        return objectiveFunction;
    }


}