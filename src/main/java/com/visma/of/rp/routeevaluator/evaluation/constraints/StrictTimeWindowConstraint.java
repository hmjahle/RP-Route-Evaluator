package com.visma.of.rp.routeevaluator.evaluation.constraints;


public class StrictTimeWindowConstraint extends CustomCriteriaConstraint {


    public StrictTimeWindowConstraint() {

        super(i -> !i.isDestination() && i.isStrict(),
                i -> i.getStartOfServiceNextTask() + i.getTask().getDuration() <= i.getTask().getEndTime());
    }

}
