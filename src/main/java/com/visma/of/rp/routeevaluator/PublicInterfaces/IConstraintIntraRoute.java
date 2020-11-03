package com.visma.of.rp.routeevaluator.PublicInterfaces;


import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

public interface IConstraintIntraRoute {

    boolean constraintIsFeasible(ConstraintInfo constraintInfo);
}
