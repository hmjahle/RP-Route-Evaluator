package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;

/**
 * Implements the ITask interface and uses it to test methods in the route evaluator.
 */
public class TestTask implements ITask {

    int duration;
    int startTime;
    int endTime;
    boolean isStrict;
    boolean isSynced;

    public void setRequirePhysicalAppearance(boolean requirePhysicalAppearance) {
        this.requirePhysicalAppearance = requirePhysicalAppearance;
    }

    boolean requirePhysicalAppearance;
    int requiredSkillLevel;
    int syncedWithIntervalDiffSeconds;
    ILocation location;

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public TestTask(int duration, int startTime, int endTime, boolean isStrict, boolean isSynced, boolean requirePhysicalAppearance, int requiredSkillLevel, int syncedWithIntervalDiffSeconds, ILocation location, String id) {
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isStrict = isStrict;
        this.isSynced = isSynced;
        this.requirePhysicalAppearance = requirePhysicalAppearance;
        this.requiredSkillLevel = requiredSkillLevel;
        this.syncedWithIntervalDiffSeconds = syncedWithIntervalDiffSeconds;
        this.location = location;
        this.id = id;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public int getEndTime() {
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
    public int getSyncedWithIntervalDiff() {
        return syncedWithIntervalDiffSeconds;
    }

    @Override
    public ILocation getLocation() {
        return location;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setStrict(boolean strict) {
        isStrict = strict;
    }
}