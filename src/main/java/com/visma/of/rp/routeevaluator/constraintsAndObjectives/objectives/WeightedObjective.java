package com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives;

public class WeightedObjective extends ObjectiveAbstract {

    public WeightedObjective(double objectiveValue) {
        super(objectiveValue);
    }

    public WeightedObjective() {
        super();
    }

    protected void updateObjective(String objectiveFunctionId, double weight, double objectiveValue) {
        this.objectiveValue += objectiveValue * weight;
    }

    protected ObjectiveAbstract initializeNewObjective() {
        return new WeightedObjective(objectiveValue);
    }

    public int dominates(ObjectiveAbstract other) {
        if (this.objectiveValue < other.objectiveValue) {
            return -1;
        } else if (this.objectiveValue > other.objectiveValue)
            return 1;
        else
            return 0;
    }

}