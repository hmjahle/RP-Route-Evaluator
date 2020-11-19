package com.visma.of.rp.routeevaluator.evaluation.constraints;


import com.visma.of.rp.routeevaluator.evaluation.info.ConstraintInfo;
import com.visma.of.rp.routeevaluator.interfaces.IConstraintIntraRoute;

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
