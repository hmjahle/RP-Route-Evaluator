package com.visma.of.rp.routeevaluator.Interfaces;

/**
 * A position refers to a physical position from which a distance to another physical position can be calculated.
 */
public interface IPosition {

    /**
     * Check whether the position is an office.
     *
     * @return True if the position is an office.
     */
    boolean isOffice();
}
