package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.IDistanceMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.ILocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the IDistanceMatrix interface and uses it to test methods in the route evaluator.
 */
public class TestDistanceMatrix implements IDistanceMatrix {

    private Map<ILocation, Map<ILocation, Long>> distances;

    public TestDistanceMatrix() {
        this.distances = new HashMap<>();
    }

    @Override
    public boolean connected(ILocation from, ILocation to) {
        return distances.containsKey(from) && distances.get(from).containsKey(to);
    }

    @Override
    public long getDistance(ILocation from, ILocation to) {
        return distances.get(from).get(to);
    }

    public void addUndirectedConnection(ILocation positionA, ILocation positionB, long distance) {
        addDirectedConnection(positionA, positionB, distance);
        addDirectedConnection(positionB, positionA, distance);
    }

    private void addDirectedConnection(ILocation positionA, ILocation positionB, long distance) {
        distances.putIfAbsent(positionA, new HashMap<>());
        distances.get(positionA).put(positionB, distance);
    }
}