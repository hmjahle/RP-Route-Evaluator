package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.IDistanceMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.IPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the IDistanceMatrix interface and uses it to test methods in the route evaluator.
 */
public class TestDistanceMatrix implements IDistanceMatrix {

    private Map<IPosition, Map<IPosition, Long>> distances;

    public TestDistanceMatrix() {
        this.distances = new HashMap<>();
    }

    @Override
    public boolean connected(IPosition from, IPosition to) {
        return distances.containsKey(from) && distances.get(from).containsKey(to);
    }

    @Override
    public long getDistance(IPosition from, IPosition to) {
        return distances.get(from).get(to);
    }

    public void addUndirectedConnection(IPosition positionA, IPosition positionB, long distance) {
        addDirectedConnection(positionA, positionB, distance);
        addDirectedConnection(positionB, positionA, distance);
    }

    private void addDirectedConnection(IPosition positionA, IPosition positionB, long distance) {
        distances.putIfAbsent(positionA, new HashMap<>());
        distances.get(positionA).put(positionB, distance);
    }
}