package com.visma.of.rp.routeevaluator.Interfaces;

public interface ITask {

    int DEFAULT_SKILL_LEVEL = 1;

    long getDurationSeconds();

    long getStartTime();

    long getEndTime();

    boolean isStrict();

    boolean isSynced();

    boolean getRequirePhysicalAppearance();

    int getRequiredSkillLevel();

    long getSyncedWithIntervalDiffSeconds();

    IPosition getPosition();
}
