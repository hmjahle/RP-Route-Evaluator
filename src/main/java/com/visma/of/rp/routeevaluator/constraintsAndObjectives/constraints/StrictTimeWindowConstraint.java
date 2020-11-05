package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;


public class StrictTimeWindowConstraint extends TimeWindowCustomCriteriaConstraint {

    public StrictTimeWindowConstraint() {
        super(i -> i != null && i.isStrict());
    }

}
