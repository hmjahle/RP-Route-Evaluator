package com.visma.of.rp.routeevaluator.physicalAppearance;

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

/**
 * This class tests the physical appearance issues under different scenarios of travel times.
 * Here waiting before, after and between non-physical appearance tasks is tested.
 */
public class NoPhysicalAppearanceTravelTimesTest extends JUnitTestAbstract {

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
        shift = new TestShift(getTime(8), getTime(16));
    }

    @Test
    public void longTimeBetweenTasksTest() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals(getTime(0, 6, 28), getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals(getTime(0, 2, 23), getVisitTravelTime(result, 2));
        Assert.assertEquals("Must return to office at: ", getTime(10, 25, 40), result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void noExtraTimeBetweenTasksTest() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(4));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals(getTime(0, 6, 28), getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals(getTime(0, 2, 7), getVisitTravelTime(result, 2));
        Assert.assertEquals("Must return to office at: ", getTime(10, 28, 51), result.getTimeOfArrivalAtDestination().longValue());
    }


    @Test
    public void onlyExtraTimeBetweenFirstAndSecondTaskTest() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(4));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals(getTime(0, 6, 28), getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals(getTime(0, 2, 7), getVisitTravelTime(result, 2));
        Assert.assertEquals("Must return to office at: ", getTime(10, 26, 44), result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void onlyExtraTimeBetweenSecondAndThirdTaskTest() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals(getTime(0, 6, 28), getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals(getTime(0, 2, 23), getVisitTravelTime(result, 2));
        Assert.assertEquals("Must return to office at: ", getTime(10, 25, 40), result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void mustStopHalfwayTest() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(5));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(6));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);
        Assert.assertEquals(getTime(0, 6, 28), getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals(getTime(0, 2, 7), getVisitTravelTime(result, 2));
        Assert.assertEquals("Must return to office at: ", getTime(10, 27, 51), result.getTimeOfArrivalAtDestination().longValue());
    }

    protected List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    protected List<ITask> createTasksTimeWindows(List<ILocation> locations) {
        TestTask task1 = new TestTask(getTime(0, 10), getTime(9), getTime(10), false, false, true, 0, 0, locations.get(0), "1");
        TestTask task4 = new TestTask(getTime(0, 10), getTime(9, 20), getTime(14), false, false, true, 0, 0, locations.get(0), "4");
        TestTask task6 = new TestTask(getTime(0, 9), getTime(9, 20), getTime(14), false, false, true, 0, 0, locations.get(0), "6");

        TestTask task3 = new TestTask(getTime(0, 20), getTime(10), getTime(11), false, false, true, 0, 0, locations.get(1), "3");

        TestTask task2 = new TestTask(getTime(0, 20), getTime(9, 30), getTime(10), false, false, false, 0, 0, locations.get(2), "2");

        TestTask task5 = new TestTask(getTime(0, 30), getTime(9, 30), getTime(14), false, false, true, 0, 0, locations.get(3), "5");
        TestTask task7 = new TestTask(getTime(0, 30), getTime(9, 51), getTime(14), false, false, true, 0, 0, locations.get(3), "7");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        tasks.add(task6);
        tasks.add(task7);
        return tasks;
    }

    private TestTravelTimeMatrix createTravelTimeMatrix(List<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix(locations, office);
        for (ILocation location : locations)
            travelTimeMatrix.addUndirectedConnection(office, location, 1000);
        travelTimeMatrix.addDirectedConnection(office, locations.get(0), getTime(0, 6, 28));
        travelTimeMatrix.addDirectedConnection(locations.get(0), locations.get(1), getTime(0, 2, 23));
        travelTimeMatrix.addDirectedConnection(locations.get(0), locations.get(3), getTime(0, 2, 7));
        travelTimeMatrix.addDirectedConnection(locations.get(0), office, getTime(0, 6, 44));
        travelTimeMatrix.addDirectedConnection(locations.get(1), office, getTime(0, 5, 40));
        travelTimeMatrix.addDirectedConnection(locations.get(3), office, getTime(0, 6, 44));
        return travelTimeMatrix;
    }

    private RouteEvaluatorResult<ITask> evaluateRoute(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
    }
}
