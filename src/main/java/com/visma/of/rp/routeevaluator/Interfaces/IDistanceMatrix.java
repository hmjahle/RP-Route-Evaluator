package com.visma.of.rp.routeevaluator.Interfaces;

/**
 * The distance matrix describes the "distance" between any two positions.
 * Note that the distance matrix might be directed, hence the from / to order matters.
 */
public interface IDistanceMatrix {

    /**
     * Describe if the two positions is connected. Which means that it is possible to move between them.
     * If this is the case a distance between the two positions MUST exist in the distance matrix.
     * @param from Position where travel starts.
     * @param to Position where travel ends.
     * @return True if they are connected otherwise false.
     */
    boolean connected(IPosition from, IPosition to);

    /**
     * Gets the distance between two points.
     * Un-described behaviour if the two points are not connected.
     * @param from Position where travel starts.
     * @param to Position where travel ends.
     * @return Distance between the two points.
     */
    long getDistance(IPosition from, IPosition to);
}
