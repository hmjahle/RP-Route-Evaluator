package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.SyncedTaskStartTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.TimeWindowObjectiveFunction;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.TravelTimeObjectiveFunction;
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
 * Tests if the synced task objective is implemented correctly.
 */
public class WeightedObjectiveTest extends JUnitTestAbstract {

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

    /**
     * Second synced task has a synced diff of 10, hence it should incur no penalty.
     */
    @Test
    public void twoSyncedTasksSyncedDiff() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(0));
        TestTask newTask = new TestTask(10, 0, 100, false, true, true, 0, 10, locations.get(6), "7");
        tasks.add(newTask);
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10L);
        syncedTaskStartTimes.put(newTask, 35L);


        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
        printResult(result);

        //Todo: synced 30, travel 30, tw 25.
        Assert.assertEquals("Objective value must be: ", 85, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void twoNonSyncedTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        printResult(result);

        //Todo: synced 0, travel 25, tw 0.
        Assert.assertEquals("Objective value must be: ", 25, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void twoSyncedTaskExtraTravelTime() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10L);
        syncedTaskStartTimes.put(allTasks.get(2), 50L);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(2).getLocation(), 31);

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
        printResult(result);

        //Todo: synced 1, travel 51, tw 1.
        Assert.assertEquals("Objective value must be: ", 53, result.getObjectiveValue(), 1E-6);
    }


    @Test
    public void sixMixedTasksRobustness() {
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 9L);
        syncedTaskStartTimes.put(allTasks.get(2), 40L);
        syncedTaskStartTimes.put(allTasks.get(4), 80L);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(1).getLocation(), 7);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(2).getLocation(), allTasks.get(3).getLocation(), 24);

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));

        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);
        printResult(result);

        //Todo: synced 11, travel 66, tw 7.
        Assert.assertEquals("Objective value must be: ", 84, result.getObjectiveValue(), 1E-6);

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
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
    }

}
