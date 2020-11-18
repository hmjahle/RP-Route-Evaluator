package com.visma.of.rp.routeevaluator.evaluation;

public class OvertimeAbstract {

    protected boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }

}
