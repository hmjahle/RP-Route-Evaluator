package com.visma.of.rp.routeevaluator.interfaces;


import com.visma.of.rp.routeevaluator.evaluation.info.ConstraintInfo;

public interface IConstraintIntraRoute {

    boolean constraintIsFeasible(ConstraintInfo constraintInfo);
}
