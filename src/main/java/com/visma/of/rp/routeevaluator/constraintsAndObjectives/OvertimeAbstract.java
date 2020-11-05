package com.visma.of.rp.routeevaluator.constraintsAndObjectives;

public class OvertimeAbstract {

    protected boolean isOverTime(long workShiftEnd, long officeReturn) {
        return officeReturn > workShiftEnd;
    }

}
