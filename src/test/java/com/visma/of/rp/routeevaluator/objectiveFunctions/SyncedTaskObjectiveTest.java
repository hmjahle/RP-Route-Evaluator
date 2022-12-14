package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.evaluation.objectives.SyncedTaskStartTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
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

/**
 * Tests if the synced task objective is implemented correctly.
 */
public class SyncedTaskObjectiveTest extends JUnitTestAbstract {

    List<ILocation> locations;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    ILocation office;
    IShift shift;

    @Before
    public void init() {
        office = createOffice();
        locations = createLocations();
        allTasks = createTasksTimeWindows(locations);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(0, 150);
    }

    @Test
    public void oneSyncedTaskFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);

        Assert.assertEquals("Objective value must be: ", 0, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void twoSyncedTasks() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10);
        syncedTaskStartTimes.put(allTasks.get(2), 50);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);

        Assert.assertEquals("Objective value must be: ", 0, result.getObjectiveValue(), 1E-6);
        double objective = evaluateRouteObjective(tasks, syncedTaskStartTimes);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
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
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10);
        syncedTaskStartTimes.put(newTask, 35);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);
        Assert.assertEquals("Objective value must be: ", 30, result.getObjectiveValue(), 1E-6);
        double objective = evaluateRouteObjective(tasks, syncedTaskStartTimes);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void twoNonSyncedTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluatorResult result = evaluateRoute(tasks, null);

        Assert.assertEquals("Objective value must be: ", 0, result.getObjectiveValue(), 1E-6);
        double objective = evaluateRouteObjective(tasks, null);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void twoSyncedTaskExtraTravelTime() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10);
        syncedTaskStartTimes.put(allTasks.get(2), 50);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(2).getLocation(), 31);
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTaskStartTimes);

        Assert.assertEquals("Objective value must be: ", 1, result.getObjectiveValue(), 1E-6);
        double objective = evaluateRouteObjective(tasks, syncedTaskStartTimes);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void twoSyncedTaskThreshold() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(2));
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 10);
        syncedTaskStartTimes.put(allTasks.get(2), 50);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(2).getLocation(), 31);
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Objective value must be: ", 0, result.getObjectiveValue(), 1E-6);
        double objective = routeEvaluator.evaluateRouteObjective(tasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void sixMixedTasks() {
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 20);
        syncedTaskStartTimes.put(allTasks.get(2), 60);
        syncedTaskStartTimes.put(allTasks.get(4), 90);
        RouteEvaluatorResult result = evaluateRoute(allTasks, syncedTaskStartTimes);

        Assert.assertEquals("Objective value must be: ", 0, result.getObjectiveValue(), 1E-6);
        double objective = evaluateRouteObjective(allTasks, syncedTaskStartTimes);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }


    @Test
    public void sixMixedTasksRobustness() {
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 9);
        syncedTaskStartTimes.put(allTasks.get(2), 40);
        syncedTaskStartTimes.put(allTasks.get(4), 80);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(1).getLocation(), 7);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(2).getLocation(), allTasks.get(3).getLocation(), 24);
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Objective value must be: ", 11, result.getObjectiveValue(), 1E-6);
        double objective = routeEvaluator.evaluateRouteObjective(allTasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    protected List<ITask> createTasksTimeWindows(List<ILocation> locations) {
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

    protected List<ILocation> createLocations() {
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

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks, Map<ITask, Integer> syncedTaskStartTimes) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
    }

    private Double evaluateRouteObjective(List<ITask> tasks, Map<ITask, Integer> syncedTaskStartTimes) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        return routeEvaluator.evaluateRouteObjective(tasks, syncedTaskStartTimes, shift);
    }

}
