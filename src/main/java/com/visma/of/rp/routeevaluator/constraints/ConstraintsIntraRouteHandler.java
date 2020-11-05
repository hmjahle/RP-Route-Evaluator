package com.visma.of.rp.routeevaluator.constraints;


import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;
import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;

import java.util.ArrayList;
import java.util.List;


public class ConstraintsIntraRouteHandler {

    List<IConstraintIntraRoute> constraints;

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
