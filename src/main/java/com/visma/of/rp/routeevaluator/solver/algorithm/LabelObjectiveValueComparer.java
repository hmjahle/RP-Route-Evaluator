package com.visma.of.rp.routeevaluator.solver.algorithm;

import java.util.Comparator;

/**
 * Used to compare two labels' objective values.
 */
 public  class LabelObjectiveValueComparer implements Comparator<Label> {

    @Override
    public int compare(Label x, Label y) {
        return Double.compare(x.getObjective().getObjectiveValue(), y.getObjective().getObjectiveValue());
    }

}