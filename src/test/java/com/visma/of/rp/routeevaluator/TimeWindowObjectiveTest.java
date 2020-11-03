package com.visma.of.rp.routeevaluator;


import com.visma.of.rp.routeevaluator.PublicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.PublicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.PublicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.PublicInterfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.objectives.TimeWindowObjective;
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
public class TimeWindowObjectiveTest extends JUnitTestAbstract {
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
        RouteEvaluatorResult result = evaluateRoute(allTasks);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Objective value must be.", 0, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals(9, getVisitTravelTime(result, 0));
        Assert.assertEquals("Must return to office at: ", 19, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void timeWindowCostOfOne() {
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        allTasks.add(task1);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 10);
        RouteEvaluatorResult result = evaluateRoute(allTasks);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Objective value must be.", 1, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals(10, getVisitTravelTime(result, 0));
        Assert.assertEquals("Must return to office at: ", 21, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void timeWindowMultipleTasksAllBreaking() {
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(3), "4");

        RouteEvaluatorResult result = evaluateRoute(task1, task2, task3, task4);
        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Objective value must be.", 70, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("Must return to office at: ", 54, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void timeWindowMultipleTasksSomeBreaking() {
        TestTask task1 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 30, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(1, 0, 50, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(1, 0, 10, false, false, true, 0, 0, locations.get(3), "4");

        RouteEvaluatorResult result = evaluateRoute(task1, task2, task3, task4);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Objective value must be.", 35, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("Must return to office at: ", 54, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void timeWindowMultipleTasksNoneBreaking() {
        TestTask task1 = new TestTask(1, 0, 50, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 30, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(1, 0, 50, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(3), "4");

        RouteEvaluatorResult result = evaluateRoute(task1, task2, task3, task4);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Objective value must be.", 0, result.getObjectiveValue(), 1E-6);
        Assert.assertEquals("Must return to office at: ", 54, result.getTimeOfOfficeReturn().longValue());
    }


    private RouteEvaluatorResult evaluateRoute(TestTask task1, TestTask task2, TestTask task3, TestTask task4) {
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);
        allTasks.add(task4);
        ITravelTimeMatrix travelTimeMatrix = createTravelTimeMatrix(office, allTasks);

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjective());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
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

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjective());
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
