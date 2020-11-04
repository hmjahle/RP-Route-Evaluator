package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;

/**
 * Implements the IShift interface and uses it to test methods in the route evaluator.
 */
public class TestShift implements IShift {

    long duration;
    long startTime;
    long endTime;

    public TestShift(long duration, long startTime, long endTime) {
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
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


}