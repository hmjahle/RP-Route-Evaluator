package com.visma.of.rp.routeevaluator.physicalAppearance;


import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestShift;
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
        allTasks = createTasksEqual(locations);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(0, 100);
    }

    @Test
    public void startWithThreeNonPhysical() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals(5, getVisitTravelTime(result, 3));
        Assert.assertEquals(13, getVisitTravelTime(result, 4));
        Assert.assertEquals("Must return to office at: ", 77, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void threeNonPhysicalInTheMiddle() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals(9, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 3));
        Assert.assertEquals(13, getVisitTravelTime(result, 4));
        Assert.assertEquals("Must return to office at: ", 77, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void threeNonPhysicalAtTheEnd() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals(5, getVisitTravelTime(result, 0));
        Assert.assertEquals(13, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 3));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 4));
        Assert.assertEquals("Must return to office at: ", 77, result.getTimeOfArrivalAtDestination().longValue());
    }


    @Test
    public void threeNonPhysicalAtTheStart() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals(5, getVisitTravelTime(result, 3));
        Assert.assertEquals(13, getVisitTravelTime(result, 4));
        Assert.assertEquals("Must return to office at: ", 77, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void everyOtherNonPhysical() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(2));

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 0));
        Assert.assertEquals(5, getVisitTravelTime(result, 1));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 2));
        Assert.assertEquals(13, getVisitTravelTime(result, 3));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 4));
        Assert.assertEquals("Must return to office at: ", 77, result.getTimeOfArrivalAtDestination().longValue());
    }



    private TestTravelTimeMatrix createTravelTimeMatrix(List<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix(locations,office);
        for(ILocation location : locations)
            travelTimeMatrix.addUndirectedConnection(office, location, 1000);

        travelTimeMatrix.addUndirectedConnection(office, locations.get(3), 5);
        travelTimeMatrix.addUndirectedConnection(office, locations.get(4), 9);
        travelTimeMatrix.addUndirectedConnection(locations.get(3), locations.get(4), 13);
        return travelTimeMatrix;
    }

    private RouteEvaluatorResult<ITask> evaluateRoute(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }
}
