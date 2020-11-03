package com.visma.of.rp.routeevaluator.PublicInterfaces;

/**
 * A location refers to a physical location from which a distance to another physical location can be calculated.
 */
public interface ILocation {

    /**
     * Check whether the location is an office.
     *
     * @return True if the location is an office.
     */
    boolean isOffice();
}
