package com.visma.of.rp.routeevaluator.constraintFunctions;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints.SyncedTasksConstraint;
import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTask;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.*;

import static benchmarking.benchmarking.printResult;

/**
 * Tests if the synced task constraint is implemented correctly.
 */
public class SyncedTaskConstraintTest extends JUnitTestAbstract {

    List<ILocation> locations;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    ILocation office;
    IShift shift;

    @Before
    public void init() {
        office = createOffice();
        locations = createLocations();
        allTasks = createTasks(locations);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(150, 0, 150);
    }

    @Test
    public void oneSyncedTaskFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10L);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void twoSyncedTaskFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 40L);
        syncedTaskStartTimes.put(allTasks.get(2), 55L);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    /**
     * Second synced task has a synced diff of 10, hence it should incur no penalty.
     */
    @Test
    public void oneSyncedTaskSyncedDiff() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        TestTask newTask = new TestTask(10, 0, 100, false, true, true, 0, 10, locations.get(6), "7");
        tasks.add(newTask);
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(newTask, 25L);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);
        printResult(result);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void twoSyncedTaskInfeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 40L);
        syncedTaskStartTimes.put(allTasks.get(2), 55L);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(2).getLocation(), 6);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void twoSyncedTaskThresholdFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 40L);
        syncedTaskStartTimes.put(allTasks.get(2), 55L);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(2).getLocation(), 6);
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new SyncedTasksConstraint(1));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void sixMixedTasksFeasible() {
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10L);
        syncedTaskStartTimes.put(allTasks.get(2), 50L);
        syncedTaskStartTimes.put(allTasks.get(4), 80L);
        RouteEvaluatorResult result = evaluateRoute(allTasks, syncedTaskStartTimes);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void sixMixedTasksInfeasible() {
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 30L);
        syncedTaskStartTimes.put(allTasks.get(2), 70L);
        syncedTaskStartTimes.put(allTasks.get(4), 90L);
        RouteEvaluatorResult result = evaluateRoute(allTasks, syncedTaskStartTimes);
        Assert.assertNull("Must be infeasible. ", result);
    }

    private List<ITask> createTasks(List<ILocation> locations) {
        TestTask task1 = new TestTask(10, 0, 20, false, true, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(10, 20, 40, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(10, 40, 60, false, true, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(10, 60, 80, false, false, true, 0, 0, locations.get(3), "4");
        TestTask task5 = new TestTask(10, 80, 100, true, true, true, 0, 0, locations.get(4), "5");
        TestTask task6 = new TestTask(10, 10, 120, true, false, true, 0, 0, locations.get(5), "6");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        tasks.add(task6);
        return tasks;
    }

    private TestTravelTimeMatrix createTravelTimeMatrix(Collection<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ILocation locationA : locations) {
            travelTimeMatrix.addUndirectedConnection(office, locationA, 10);
            for (ILocation locationB : locations)
                if (locationA != locationB)
                    travelTimeMatrix.addUndirectedConnection(locationA, locationB, 5);
        }
        return travelTimeMatrix;
    }

    private List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks, Map<ITask, Long> syncedTaskStartTimes) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new SyncedTasksConstraint());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
    }

}
