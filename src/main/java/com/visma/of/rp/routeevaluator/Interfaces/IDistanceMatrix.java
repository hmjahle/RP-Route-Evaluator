package com.visma.of.rp.routeevaluator.Interfaces;

/**
 * The distance matrix describes the "distance" between any two locations.
 * Note that the distance matrix might be directed, hence the from / to order matters.
 */
public interface IDistanceMatrix {

    /**
     * Describe if the two locations is connected. Which means that it is possible to move between them.
     * If this is the case a distance between the two locations MUST exist in the distance matrix.
     * @param from Location where travel starts.
     * @param to Location where travel ends.
     * @return True if they are connected otherwise false.
     */
    boolean connected(ILocation from, ILocation to);

    /**
     * Gets the distance between two points.
     * Un-described behaviour if the two points are not connected.
     * @param from Location where travel starts.
     * @param to Location where travel ends.
     * @return Distance between the two points.
     */
    long getDistance(ILocation from, ILocation to);
}
