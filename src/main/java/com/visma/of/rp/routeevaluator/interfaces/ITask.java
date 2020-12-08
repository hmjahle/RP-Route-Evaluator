package com.visma.of.rp.routeevaluator.interfaces;

public interface ITask {

    /**
     * The duration of the task.
     *
     * @return Duration in seconds.
     */
    int getDuration();

    /**
     * The start time of the task.
     *
     * @return Start time in seconds.
     */
    int getStartTime();

    /**
     * The end time of the task.
     *
     * @return End time in seconds.
     */
    int getEndTime();

    /**
     * Whether the task has a strict time-window.
     *
     * @return True if it is strict.
     */
    boolean isStrict();

    /**
     * Whether the task is synced with other tasks.
     *
     * @return True if it is synced.
     */
    boolean isSynced();

    /**
     * Whether the task requires physical appearance to be fulfilled.
     * @return True if it does.
     */
    boolean getRequirePhysicalAppearance();

    /**
     * The maximum difference from the start time of the task that the task is synced with.
     * @return Time difference in seconds.
     */
    int getSyncedWithIntervalDiff();

    /**
     * The location where the task has to be performed.
     * @return The tasks location or @null if the task has no physical appearance.
     */
    ILocation getLocation();

    /**
     * Unique task id.
     * @return Task id as string.
     */
    String getId();

}
