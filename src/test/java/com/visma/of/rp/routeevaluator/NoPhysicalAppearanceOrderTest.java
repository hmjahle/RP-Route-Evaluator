package com.visma.of.rp.routeevaluator;


import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteSimulatorResult;
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
import java.util.List;

/**
 * Tests if the order of non-physical appearance tasks is handled correctly.
 */
public class NoPhysicalAppearanceOrderTest extends JUnitTestAbstract {

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
        shift = new TestShift(100, 0, 100);
    }

    @Test
    public void startWithThreeNonPhysical() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));

        RouteSimulatorResult result = evaluateRoute(tasks);

        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals(5, getVisitTravelTime(result, 3));
        Assert.assertEquals(13, getVisitTravelTime(result, 4));
        Assert.assertEquals(77, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void threeNonPhysicalInTheMiddle() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));

        RouteSimulatorResult result = evaluateRoute(tasks);

        Assert.assertEquals(9, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 3));
        Assert.assertEquals(13, getVisitTravelTime(result, 4));
        Assert.assertEquals(77, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void threeNonPhysicalAtTheEnd() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));

        RouteSimulatorResult result = evaluateRoute(tasks);

        Assert.assertEquals(5, getVisitTravelTime(result, 0));
        Assert.assertEquals(13, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 3));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 4));
        Assert.assertEquals(77, result.getTimeOfOfficeReturn().longValue());
    }



    @Test
    public void threeNonPhysicalAtTheStart() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));

        RouteSimulatorResult result = evaluateRoute(tasks);

        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals(5, getVisitTravelTime(result, 3));
        Assert.assertEquals(13, getVisitTravelTime(result, 4));
        Assert.assertEquals(77, result.getTimeOfOfficeReturn().longValue());
    }

    @Test
    public void everyOtherNonPhysical() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(2));

        RouteSimulatorResult result = evaluateRoute(tasks);

        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 0));
        Assert.assertEquals(5, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals(13, getVisitTravelTime(result, 3));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 4));
        Assert.assertEquals(77, result.getTimeOfOfficeReturn().longValue());
    }
    private List<ITask> createTasks(List<ILocation> locations) {
        TestTask task1 = new TestTask(10, 0, 100, false, false, false, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(10, 0, 100, false, false, false, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(10, 0, 100, false, false, false, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(3), "4");
        TestTask task5 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(4), "5");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        return tasks;
    }

    private TestTravelTimeMatrix createTravelTimeMatrix(List<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        travelTimeMatrix.addUndirectedConnection(office, locations.get(3), 5);
        travelTimeMatrix.addUndirectedConnection(office, locations.get(4), 9);
        travelTimeMatrix.addUndirectedConnection(locations.get(3), locations.get(4), 13);
        return travelTimeMatrix;
    }

    private List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    private RouteSimulatorResult evaluateRoute(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        return routeEvaluator.simulateRouteByTheOrderOfTasks(tasks, null, shift);
    }
}
