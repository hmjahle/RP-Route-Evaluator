package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.ILocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the ITravelTimeMatrix interface and uses it to test methods in the route evaluator.
 */
public class TestTravelTimeMatrix implements ITravelTimeMatrix {

    private Map<ILocation, Map<ILocation, Long>> travelTimes;

    public TestTravelTimeMatrix() {
        this.travelTimes = new HashMap<>();
    }

    @Override
    public boolean connected(ILocation from, ILocation to) {
        return travelTimes.containsKey(from) && travelTimes.get(from).containsKey(to);
    }

    @Override
    public long getTravelTime(ILocation from, ILocation to) {
        return travelTimes.get(from).get(to);
    }

    public void addUndirectedConnection(ILocation positionA, ILocation positionB, long distance) {
        addDirectedConnection(positionA, positionB, distance);
        addDirectedConnection(positionB, positionA, distance);
    }

    private void addDirectedConnection(ILocation positionA, ILocation positionB, long distance) {
        travelTimes.putIfAbsent(positionA, new HashMap<>());
        travelTimes.get(positionA).put(positionB, distance);
    }
}