package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.evaluation.objectives.SyncedTaskStartTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TimeWindowObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TravelTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjectiveWithValues;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests if the synced task objective is implemented correctly.
 */
public class IObjectiveTest extends JUnitTestAbstract {

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

        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        RouteEvaluatorResult<ITask> result = getRouteEvaluatorResult(tasks, syncedTaskStartTimes, routeEvaluator);
        Assert.assertEquals("Unweighted objective value: ", 85, result.getObjectiveValue(), 1E-6);

        routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        addWeightedObjectives(routeEvaluator, 0);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Weighted objective value: ", 3003025, result.getObjectiveValue(), 1E-6);

        routeEvaluator.getObjectiveFunctions().updateObjectiveWeight("Sync", 1);
        routeEvaluator.getObjectiveFunctions().updateObjectiveWeight("TW", 1);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Weighted objective value after adjusting weights: ", 85, result.getObjectiveValue(), 1E-6);

        WeightedObjectiveWithValues obj = evaluateRouteStoreObjectiveValues(routeEvaluator, tasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Synced objective value: ", 30, obj.getObjectiveFunctionValue("Sync"), 1E-6);
        Assert.assertEquals("Time window objective value: ", 30, obj.getObjectiveFunctionValue("TW"), 1E-6);
        Assert.assertEquals("Travel time objective value: ", 25, obj.getObjectiveFunctionValue("TT"), 1E-6);

    }

    @Test
    public void twoNonSyncedTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluator<ITask> routeEvaluator = createRouteEvaluator(tasks);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
        Assert.assertEquals("Unweighted objective value: ", 25, result.getObjectiveValue(), 1E-6);

        routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        addWeightedObjectives(routeEvaluator, 0);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
        Assert.assertEquals("Weighted objective value: ", 2500, result.getObjectiveValue(), 1E-6);

        WeightedObjectiveWithValues obj = evaluateRouteStoreObjectiveValues(routeEvaluator, tasks, shift);
        Assert.assertEquals("Time window objective value: ", 25, obj.getObjectiveFunctionValue("TW"), 1E-6);

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


        RouteEvaluatorResult<ITask> result = getRouteEvaluatorResult(tasks, syncedTaskStartTimes);
        RouteEvaluator<ITask> routeEvaluator;
        Assert.assertEquals("Unweighted objective value: ", 53, result.getObjectiveValue(), 1E-6);

        routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        addWeightedObjectives(routeEvaluator, 0);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Weighted objective value: ", 105101, result.getObjectiveValue(), 1E-6);

        WeightedObjectiveWithValues obj = evaluateRouteStoreObjectiveValues(routeEvaluator, tasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Synced objective value: ", 1, obj.getObjectiveFunctionValue("Sync"), 1E-6);
        Assert.assertEquals("Time window objective value: ", 51, obj.getObjectiveFunctionValue("TW"), 1E-6);
        Assert.assertEquals("Travel time objective value: ", 1, obj.getObjectiveFunctionValue("TT"), 1E-6);

    }

    @Test
    public void sixMixedTasksRobustness() {
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 9);
        syncedTaskStartTimes.put(allTasks.get(2), 40);
        syncedTaskStartTimes.put(allTasks.get(4), 80);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(0).getLocation(), allTasks.get(1).getLocation(), 7);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(2).getLocation(), allTasks.get(3).getLocation(), 24);

        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        addObjectives(routeEvaluator, 1);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Unweighted objective value: ", 84, result.getObjectiveValue(), 1E-6);

        routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        addWeightedObjectives(routeEvaluator, 1);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Weighted objective value: ", 1106607, result.getObjectiveValue(), 1E-6);

        WeightedObjectiveWithValues obj = evaluateRouteStoreObjectiveValues(routeEvaluator, allTasks, syncedTaskStartTimes, shift);
        Assert.assertEquals("Synced objective value: ", 11, obj.getObjectiveFunctionValue("Sync"), 1E-6);
        Assert.assertEquals("Time window objective value: ", 66, obj.getObjectiveFunctionValue("TW"), 1E-6);
        Assert.assertEquals("Travel time objective value: ", 7, obj.getObjectiveFunctionValue("TT"), 1E-6);

    }


    private RouteEvaluatorResult<ITask> getRouteEvaluatorResult(List<ITask> tasks, Map<ITask, Integer> syncedTaskStartTimes, RouteEvaluator<ITask> routeEvaluator) {
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
    }


    private RouteEvaluatorResult<ITask> getRouteEvaluatorResult(List<ITask> tasks, Map<ITask, Integer> syncedTaskStartTimes) {
        RouteEvaluator<ITask> routeEvaluator = createRouteEvaluator(tasks);
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTaskStartTimes, shift);
    }

    private RouteEvaluator<ITask> createRouteEvaluator(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        addObjectives(routeEvaluator, 0);
        return routeEvaluator;
    }

    private WeightedObjectiveWithValues evaluateRouteStoreObjectiveValues(RouteEvaluator<ITask> routeEvaluator, List<ITask> tasks,
                                                                          Map<ITask, Integer> syncedTasksStartTime, IShift employeeWorkShift) {
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByOrderOfTasksWithObjectiveValues(tasks,
                syncedTasksStartTime, employeeWorkShift);
        return (WeightedObjectiveWithValues) result.getObjective();
    }

    private WeightedObjectiveWithValues evaluateRouteStoreObjectiveValues(RouteEvaluator<ITask> routeEvaluator, List<ITask> tasks,
                                                                          IShift employeeWorkShift) {
        return evaluateRouteStoreObjectiveValues(routeEvaluator, tasks, null, employeeWorkShift);
    }

    private void addObjectives(RouteEvaluator<ITask> routeEvaluator, int slack) {
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(slack));
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
    }

    private void addWeightedObjectives(RouteEvaluator<ITask> routeEvaluator, int slack) {
        routeEvaluator.addObjectiveIntraShift("Sync", 100000, new SyncedTaskStartTimeObjectiveFunction(slack));
        routeEvaluator.addObjectiveIntraShift("TW", 100, new TravelTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift("TT", 1, new TimeWindowObjectiveFunction());
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

}
