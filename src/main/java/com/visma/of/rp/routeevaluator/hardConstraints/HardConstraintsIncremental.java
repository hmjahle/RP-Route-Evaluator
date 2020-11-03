package com.visma.of.rp.routeevaluator.hardConstraints;


import com.visma.of.rp.routeevaluator.PublicInterfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

import java.util.ArrayList;
import java.util.List;


public class HardConstraintsIncremental {

    List<IConstraintIntraRoute> hardConstraints;

    public HardConstraintsIncremental() {
        hardConstraints = new ArrayList<>();
    }

    public boolean isFeasible(ConstraintInfo constraintInfo) {
        for (IConstraintIntraRoute constraint : hardConstraints) {
            if (!constraint.constraintIsFeasible(constraintInfo)) {
                return false;
            }
        }
        return true;
    }

    void addHardConstraint(IConstraintIntraRoute constraint) {
        hardConstraints.add(constraint);
    }
}
