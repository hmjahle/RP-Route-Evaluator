package com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;

import java.util.ArrayList;
import java.util.List;


public class ConstraintsIntraRouteHandler {

    private List<IConstraintIntraRoute> constraints;

    public ConstraintsIntraRouteHandler() {
        constraints = new ArrayList<>();
    }

    public boolean isFeasible(ConstraintInfo constraintInfo) {
        for (IConstraintIntraRoute constraint : constraints) {
            if (!constraint.constraintIsFeasible(constraintInfo)) {
                return false;
            }
        }
        return true;
    }

    public void addConstraint(IConstraintIntraRoute constraint) {
        constraints.add(constraint);
    }
}