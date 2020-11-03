package com.visma.of.rp.routeevaluator.PublicInterfaces;

import com.visma.of.rp.routeevaluator.intraRouteEvaluation.hardConstraints.ConstraintInfo;

public interface IConstraintIntraRoute {

    boolean constraintIsFeasible(ConstraintInfo constraintInfo);
}
