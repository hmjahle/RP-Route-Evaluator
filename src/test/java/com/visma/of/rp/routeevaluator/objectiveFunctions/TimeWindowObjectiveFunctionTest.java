package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.TimeWindowLowHighObjectiveFunction;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.TimeWindowObjectiveFunction;
import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests if the Time window objective return the correct objective values.
 */
public class TimeWindowObjectiveFunctionTest extends JUnitTestAbstract {
    ILocation office;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    IShift shift;
    List<ILocation> locations;

    @Before
    public void init() {
        office = createOffice();
        locations = createLocations();
        allTasks = createTaskList();
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(100, 0, 100);
    }

    @Test
    public void timeWindowNoCost() {
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        allTasks.add(task1);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 9);
        RouteEvaluatorResult result = evaluateRouteStandardTimeWindow(allTasks);
        init();
        RouteEvaluatorResult resultLowHigh = evaluateRouteLowHighTimeWindow(allTasks, 100, 1);
        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("WeightedObjective value for the standard time window must be.", 0, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("WeightedObjective value for the low/high time window must be.", 0, resultLowHigh.getObjectiveValue(), 1E-6);
        Assert.assertEquals(9, getVisitTravelTime(result, 0));
        Assert.assertEquals("Must return to office at: ", 19, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void timeWindowCostOfOne() {
        initTest();
        RouteEvaluatorResult result = evaluateRouteStandardTimeWindow(allTasks);
        initTest();
        RouteEvaluatorResult resultLowHigh = evaluateRouteLowHighTimeWindow(allTasks, 100, 0);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("WeightedObjective value for the standard time window must be.", 1, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("WeightedObjective value for the low/high time window must be.", 100, resultLowHigh.getObjectiveValue(), 1E-6);
        Assert.assertEquals(10, getVisitTravelTime(result, 0));
        Assert.assertEquals("Must return to office at: ", 21, result.getTimeOfOfficeReturn().longValue());
    }

    private void initTest() {
        init();
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        allTasks.add(task1);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 10);
    }

    @Test
    public void timeWindowMultipleTasksAllBreaking() {
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(3), "4");

        RouteEvaluatorResult result = evaluateRouteStandardTimeWindow(task1, task2, task3, task4);
        init();
        RouteEvaluatorResult resultLowHigh = evaluateRouteLowHighTimeWindow(task1, task2, task3, task4, 1000, 10);
        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("WeightedObjective value for the standard time window must be.", 70, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("WeightedObjective value for the low/high time window must be.", 39031, resultLowHigh.getObjectiveValue(), 1E-6);
        Assert.assertEquals("Must return to office at: ", 54, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void timeWindowMultipleTasksSomeBreaking() {
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 30, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(1, 0, 50, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(3), "4");

        RouteEvaluatorResult resultLowHigh = evaluateRouteLowHighTimeWindow(task1, task2, task3, task4, 1000, 4);
        init();
        RouteEvaluatorResult result = evaluateRouteStandardTimeWindow(task1, task2, task3, task4);

        Assert.assertEquals("WeightedObjective value for the standard time window must be.", 35, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("WeightedObjective value for the low/high time window must be.", 30005, resultLowHigh.getObjectiveValue(), 1E-6);
    }

    @Test
    public void timeWindowMultipleTasksNoneBreaking() {
        TestTask task1 = new TestTask(1, 0, 50, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 30, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(1, 0, 50, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(3), "4");

        RouteEvaluatorResult result = evaluateRouteStandardTimeWindow(task1, task2, task3, task4);
        init();
        RouteEvaluatorResult resultLowHigh = evaluateRouteLowHighTimeWindow(task1, task2, task3, task4, 100, 1);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("WeightedObjective value for the standard time window must be.", 0, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("WeightedObjective value for the low/high time window must be.", 0, resultLowHigh.getObjectiveValue(), 1E-6);
        Assert.assertEquals("Must return to office at: ", 54, result.getTimeOfOfficeReturn().longValue());
    }

    private RouteEvaluatorResult evaluateRouteStandardTimeWindow(TestTask task1, TestTask task2, TestTask task3, TestTask task4) {
        RouteEvaluator routeEvaluator = buildEvaluator(task1, task2, task3, task4);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
    }

    private RouteEvaluatorResult evaluateRouteLowHighTimeWindow(TestTask task1, TestTask task2, TestTask task3, TestTask task4, double highMultiplier, long highCutOff) {
        RouteEvaluator routeEvaluator = buildEvaluator(task1, task2, task3, task4);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowLowHighObjectiveFunction(highCutOff, highMultiplier));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
    }

    private RouteEvaluator buildEvaluator(TestTask task1, TestTask task2, TestTask task3, TestTask task4) {
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);
        allTasks.add(task4);
        ITravelTimeMatrix travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        return new RouteEvaluator(0, travelTimeMatrix, allTasks, office);
    }

    private TestTravelTimeMatrix createTravelTimeMatrix(List<ILocation> locations, ILocation office) {
        return new TestTravelTimeMatrix();
    }

    private List<ITask> createTaskList() {
        return new ArrayList<>();
    }

    private List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    private RouteEvaluatorResult evaluateRouteStandardTimeWindow(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }

    private RouteEvaluatorResult evaluateRouteLowHighTimeWindow(List<ITask> tasks, double highMultiplier, long highCutOff) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowLowHighObjectiveFunction(highCutOff, highMultiplier));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }

    private ITravelTimeMatrix createTravelTimeMatrix(ILocation office, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, taskA.getLocation(), 10);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 10);
        }
        return travelTimeMatrix;
    }
}
