package com.visma.of.rp.routeevaluator.interfaces;

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
