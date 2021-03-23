package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.IShift;

/**
 * Implements the IShift interface and uses it to test methods in the route evaluator.
 */
public class TestShift implements IShift {

    int startTime;
    int endTime;

    public TestShift(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public int getDuration() {
        return endTime - startTime;
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
    public short getId() {
        return 0;
    }
}