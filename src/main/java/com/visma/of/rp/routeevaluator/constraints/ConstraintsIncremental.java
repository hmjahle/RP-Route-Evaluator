package com.visma.of.rp.routeevaluator.constraints;


import com.visma.of.rp.routeevaluator.publicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

import java.util.ArrayList;
import java.util.List;


public class ConstraintsIncremental {

    List<IConstraintIntraRoute> constraints;

    public ConstraintsIncremental() {
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

    void addConstraint(IConstraintIntraRoute constraint) {
        constraints.add(constraint);
    }
}
