package com.visma.of.rp.routeevaluator.publicInterfaces;

/**
 * The travelTime matrix describes the "travelTime" between any two locations.
 * Note that the travelTime matrix might be directed, hence the from / to order matters.
 */
public interface ITravelTimeMatrix {

    /**
     * Describe if the two locations is connected. Which means that it is possible to move between them.
     * If this is the case a travelTime between the two locations MUST exist in the travelTime matrix.
     * @param from Location where travel starts.
     * @param to Location where travel ends.
     * @return True if they are connected otherwise false.
     */
    boolean connected(ILocation from, ILocation to);

    /**
     * Gets the travelTime between two points.
     * Un-described behaviour if the two points are not connected.
     * @param from Location where travel starts.
     * @param to Location where travel ends.
     * @return TravelTime between the two points.
     */
    long getTravelTime(ILocation from, ILocation to);
}
