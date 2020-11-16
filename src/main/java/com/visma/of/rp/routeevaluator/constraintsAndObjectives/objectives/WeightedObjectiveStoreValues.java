package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.IObjective;

import java.util.HashMap;
import java.util.Map;

/**
 * Extends the weighted objective and stores the objective values in addition.
 */
public class WeightedObjectiveStoreValues extends WeightedObjective {


    Map<String, Double> objectiveFunctionValues;

    public WeightedObjectiveStoreValues(double objectiveValue) {
        super(objectiveValue);
        objectiveFunctionValues = new HashMap<>();

    }

    public WeightedObjectiveStoreValues() {
        this(0);
    }

    public WeightedObjectiveStoreValues(WeightedObjectiveStoreValues other) {
        super(other.objectiveValue);
        this.objectiveFunctionValues = new HashMap<>(other.objectiveFunctionValues);
    }


    /**
     * Adds the product of the weight and value of the objective to the objective value.
     * And stores the individual values such that they can be retrieved later.
     *
     * @param objectiveFunctionId Id of the objective function.
     * @param weight              Weight of the objective.
     * @param value               Value of the objective.
     */
    @Override
    public void incrementObjective(String objectiveFunctionId, double weight, double value) {
        this.objectiveValue += value * weight;
        double currentValue = objectiveFunctionValues.getOrDefault(objectiveFunctionId, 0.0);
        objectiveFunctionValues.put(objectiveFunctionId, currentValue + value);
    }

    @Override
    public IObjective initializeNewObjective() {
        return new WeightedObjectiveStoreValues(this);
    }

    /**
     * Gets the value for the objective function. Will return null if the id of the objective function is not
     * present in the objective.
     *
     * @param objectiveFunctionId The id of the objective function.
     * @return Double or null.
     */
    public Double getObjectiveFunctionValue(String objectiveFunctionId) {
        return objectiveFunctionValues.getOrDefault(objectiveFunctionId, null);
    }
}