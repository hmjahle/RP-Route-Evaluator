package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the ITravelTimeMatrix interface and uses it to test methods in the route evaluator.
 */
public class TestTravelTimeMatrix implements ITravelTimeMatrix {

    private Map<ILocation, Map<ILocation, Integer>> travelTimes;

    public TestTravelTimeMatrix() {
        this.travelTimes = new HashMap<>();
    }

    public TestTravelTimeMatrix(Collection<ITask> tasks) {
        this.travelTimes = new HashMap<>();
        for (ITask task : tasks)
            addUndirectedConnection(task.getLocation(), task.getLocation(), 0);
    }

    public TestTravelTimeMatrix(Collection<ILocation> locations, ILocation office) {
        this.travelTimes = new HashMap<>();
        for (ILocation location : locations)
            addUndirectedConnection(location, location, 0);
        addUndirectedConnection(office, office, 0);

    }

    @Override
    public boolean connected(ILocation from, ILocation to) {
        return travelTimes.containsKey(from) && travelTimes.get(from).containsKey(to);
    }

    @Override
    public int getTravelTime(ILocation from, ILocation to) {
        return travelTimes.get(from).get(to);
    }

    @Override
    public Collection<ILocation> getLocations() {
        return travelTimes.keySet();
    }

    public void addUndirectedConnection(ILocation locationA, ILocation locationB, int distance) {
        addDirectedConnection(locationA, locationB, distance);
        addDirectedConnection(locationB, locationA, distance);
    }

    public void addDirectedConnection(ILocation locationA, ILocation locationB, int distance) {
        checkIfLocationExists(locationA);
        checkIfLocationExists(locationB);
        travelTimes.get(locationA).put(locationB, distance);
    }

    private void checkIfLocationExists(ILocation location) {
        if (!travelTimes.containsKey(location)) {
            travelTimes.put(location, new HashMap<>());
            travelTimes.get(location).put(location, 0);
        }
    }

    public void removeDirectedConnection(ILocation locationA, ILocation locationB) {
        if (travelTimes.containsKey(locationA))
            travelTimes.get(locationA).remove(locationB);

    }
}