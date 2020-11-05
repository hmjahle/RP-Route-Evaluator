package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;


public class StrictTimeWindowConstraint extends TimeWindowCustomCriteriaConstraint {

    public StrictTimeWindowConstraint() {
        super(i -> i != null && i.isStrict(), 0);
    }

    public StrictTimeWindowConstraint(long allowedSlack) {
        super(i -> i != null && i.isStrict(), allowedSlack);
    }

}
