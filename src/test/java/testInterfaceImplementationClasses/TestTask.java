package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.IPosition;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;

/**
 * Implements the ITask interface and uses it to test methods in the route evaluator.
 */
public class TestTask implements ITask {

    long duration;
    long startTime;
    long endTime;
    boolean isStrict;
    boolean isSynced;
    boolean requirePhysicalAppearance;
    int requiredSkillLevel;
    long syncedWithIntervalDiffSeconds;
    IPosition position;

    public TestTask(long duration, long startTime, long endTime, boolean isStrict, boolean isSynced, boolean requirePhysicalAppearance, int requiredSkillLevel, long     syncedWithIntervalDiffSeconds, IPosition position) {
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isStrict = isStrict;
        this.isSynced = isSynced;
        this.requirePhysicalAppearance = requirePhysicalAppearance;
        this.requiredSkillLevel = requiredSkillLevel;
        this.syncedWithIntervalDiffSeconds = syncedWithIntervalDiffSeconds;
        this.position = position;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public boolean isStrict() {
        return isStrict;
    }

    @Override
    public boolean isSynced() {
        return isSynced;
    }

    @Override
    public boolean getRequirePhysicalAppearance() {
        return requirePhysicalAppearance;
    }

    @Override
    public int getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    @Override
    public long getSyncedWithIntervalDiff() {
        return syncedWithIntervalDiffSeconds;
    }

    @Override
    public IPosition getPosition() {
        return position;
    }
}