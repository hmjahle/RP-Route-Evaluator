package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.IShift;

/**
 * Implements the IShift interface and uses it to test methods in the route evaluator.
 */
public class TestShift implements IShift {

    long startTime;
    long endTime;

    public TestShift(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public long getDuration() {
        return endTime - startTime;
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