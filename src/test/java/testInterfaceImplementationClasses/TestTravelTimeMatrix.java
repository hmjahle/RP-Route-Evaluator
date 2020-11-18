package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;

import java.util.Collection;
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

    @Override
    public Collection<ILocation> getLocations() {
        return travelTimes.keySet();
    }

    public void addUndirectedConnection(ILocation locationA, ILocation locationB, long distance) {
        addDirectedConnection(locationA, locationB, distance);
        addDirectedConnection(locationB, locationA, distance);
    }

    public void addDirectedConnection(ILocation locationA, ILocation locationB, long distance) {
        travelTimes.putIfAbsent(locationA, new HashMap<>());
        travelTimes.get(locationA).put(locationB, distance);
    }
}