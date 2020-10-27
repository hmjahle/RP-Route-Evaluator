package com.visma.of.rp.routeevaluator.Interfaces;

import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;

/**
 * The shift interface is used to describe the essential information about a shift.
 */
public interface IShift {

    /**
     * The duration of a shift.
     *
     * @return Duration in seconds.
     */
    long getDuration();

    /**
     * The transport mode used during the shift.
     *
     * @return A transport mode.
     */
    TransportMode getTransport();

    /**
     * The start time of the shift.
     *
     * @return Start time in seconds.
     */
    long getStartTime();

    /**
     * The end time of the shift.
     *
     * @return End time in seconds.
     */
    long getEndTime();
}
