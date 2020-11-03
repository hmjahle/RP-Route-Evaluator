package com.visma.of.rp.routeevaluator.publicInterfaces;


import com.visma.of.rp.routeevaluator.intraRouteEvaluationInfo.ConstraintInfo;

public interface IConstraintIntraRoute {

    boolean constraintIsFeasible(ConstraintInfo constraintInfo);
}
