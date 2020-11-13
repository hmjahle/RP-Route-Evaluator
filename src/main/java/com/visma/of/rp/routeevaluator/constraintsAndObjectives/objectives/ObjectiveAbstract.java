package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

/**
 * Calculates objective values and check whether one objective dominates the other.
 */
public abstract class ObjectiveAbstract {

    protected double objectiveValue;

    protected ObjectiveAbstract(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    protected ObjectiveAbstract() {
        this.objectiveValue = 0;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    protected abstract void updateObjective(String objectiveFunctionId, double weight, double objectiveValue);

    protected abstract ObjectiveAbstract initializeNewObjective();

    public abstract int dominates(ObjectiveAbstract other);

    @Override
    public String toString() {
        return Double.toString(objectiveValue);
    }
}
