package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.PublicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.PublicInterfaces.ITask;

/**
 * Implements the ITask interface and uses it to test methods in the route evaluator.
 */
public class TestTask implements ITask {

    long duration;
    long startTime;
    long endTime;
    boolean isStrict;
    boolean isSynced;

    public void setRequirePhysicalAppearance(boolean requirePhysicalAppearance) {
        this.requirePhysicalAppearance = requirePhysicalAppearance;
    }

    boolean requirePhysicalAppearance;
    int requiredSkillLevel;
    long syncedWithIntervalDiffSeconds;
    ILocation location;
    String id;

    public TestTask(long duration, long startTime, long endTime, boolean isStrict, boolean isSynced, boolean requirePhysicalAppearance, int requiredSkillLevel, long syncedWithIntervalDiffSeconds, ILocation location, String id) {
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
    public ILocation getLocation() {
        return location;
    }

    @Override
    public String getId() {
        return id;
    }

}