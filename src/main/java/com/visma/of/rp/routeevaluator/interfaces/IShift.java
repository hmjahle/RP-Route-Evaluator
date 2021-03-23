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
    int getDuration();


    /**
     * The start time of the shift.
     *
     * @return Start time in seconds.
     */
    int getStartTime();

    /**
     * The end time of the shift.
     *
     * @return End time in seconds.
     */
    int getEndTime();

    /**
     * The unique id of the shift, must be continuously numbered starting from 0.
     *
     * @return Id short value.
     */
    short getId();
}
