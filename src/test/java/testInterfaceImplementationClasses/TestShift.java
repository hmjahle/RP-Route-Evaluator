package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;

/**
 * Implements the IShift interface and uses it to test methods in the route evaluator.
 */
public class TestShift implements IShift {

    long duration;
    long startTime;
    long endTime;
    TransportMode transportMode;

    public TestShift(long duration, long startTime, long endTime, TransportMode transportMode) {
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transportMode = transportMode;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public TransportMode getTransport() {
        return transportMode;
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