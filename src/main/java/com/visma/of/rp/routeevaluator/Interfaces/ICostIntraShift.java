package com.visma.of.rp.routeevaluator.Interfaces;

import com.visma.of.rp.routeevaluator.costFunctions.IncrementalCostInfo;

public interface ICostIntraShift {

    double getIncrementalCostFor(IncrementalCostInfo incrementalCostInfo);
}
