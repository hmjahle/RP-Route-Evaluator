package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.Interfaces.ICostIntraShift;
import com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.IncrementalCostInfo;


import java.util.ArrayList;
import java.util.List;


public class IncrementalCostHandler {
    private List<ICostIntraShift> intraShiftCosts;

    public IncrementalCostHandler() {
        intraShiftCosts = new ArrayList<>();
    }

    public void addCostIntraShift(ICostIntraShift costIntraShift) {
        intraShiftCosts.add(costIntraShift);
    }

    public double calculateIncrementalCost(IncrementalCostInfo incrementalCostInfo) {
        double incrementalCost = 0.0;
        for (ICostIntraShift intraShiftCost : intraShiftCosts)
            incrementalCost += intraShiftCost.getIncrementalCostFor(incrementalCostInfo);
        return incrementalCost;
    }
}
