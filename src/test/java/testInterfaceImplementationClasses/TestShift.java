package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;

/**
 * Implements the IShift interface and uses it to test methods in the route evaluator.
 */
public class TestShift implements IShift {

    int startTime;
    int endTime;
    int transportMode;
    private ILocation startLocation;
    private ILocation endLocation;

    public TestShift(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.transportMode = 1;
    }

    public TestShift(int startTime, int endTime, ILocation startLocation, ILocation endLocation) {
        this(startTime, endTime);
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public ILocation getStartLocation() {
        return startLocation;
    }

    public ILocation getEndLocation() {
        return endLocation;
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

    @Override
    public int getTransportMode() {
        return transportMode;
    }
}