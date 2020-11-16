package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.IObjective;

/**
 * A weighted objective calculates the objectives value by adding the sum of weights multiplied by the values of the objectives.
 */
public class WeightedObjective implements IObjective {

    double objectiveValue;

    public WeightedObjective(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public WeightedObjective() {
        this(0);
    }

    /**
     * Adds the product of the weight and value of the objective to the objective value.
     * @param objectiveFunctionId Id of the objective function.
     * @param weight              Weight of the objective.
     * @param value               Value of the objective.
     */
    @Override
    public void incrementObjective(String objectiveFunctionId, double weight, double value) {
        this.objectiveValue += value * weight;
    }

    @Override
    public IObjective initializeNewObjective() {
        return new WeightedObjective(objectiveValue);
    }

    @Override
    public double getObjectiveValue() {
        return objectiveValue;
    }

    @Override
    public int dominates(IObjective other) {
        return Double.compare(this.objectiveValue, other.getObjectiveValue());
    }


}