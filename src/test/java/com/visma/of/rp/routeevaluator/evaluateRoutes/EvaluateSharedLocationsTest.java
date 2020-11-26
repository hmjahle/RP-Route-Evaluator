package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
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
import java.util.Collection;
import java.util.List;

public class EvaluateSharedLocationsTest extends JUnitTestAbstract {

    ILocation origin;
    ILocation destination;
    List<ILocation> locations;
    ITravelTimeMatrix travelTimeMatrix;
    IShift shift;

    @Before
    public void initialize() {
        origin = createOffice();
        destination = createOffice();
        locations = createLocations();
        travelTimeMatrix = createTravelTimeMatrix(origin, destination, locations);
        shift = new TestShift(0, 100);
    }

    @Test
    public void multipleTasks() {
        List<ITask> tasks = createTasksTwoWithSame();

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, origin, destination);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Start time should be: ", 2, result.getVisitSolution().get(0).getTravelTime());
        Assert.assertEquals("Start time should be: ", 1, result.getVisitSolution().get(1).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(2).getTravelTime());
        Assert.assertEquals("Start time should be: ", 1, result.getVisitSolution().get(3).getTravelTime());
        Assert.assertEquals("Office return should be: ", 18, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void multipleTasksSingleLocation() {
        List<ITask> tasks = createTasksAllSame();

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, origin, destination);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Start time should be: ", 2, result.getVisitSolution().get(0).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(1).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(2).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(3).getTravelTime());
        Assert.assertEquals("Office return should be: ", 16, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void multipleTasksAllAtOffice() {
        List<ITask> tasks = createTasksAllSame();

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, locations.get(0), locations.get(0));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(0).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(1).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(2).getTravelTime());
        Assert.assertEquals("Start time should be: ", 0, result.getVisitSolution().get(3).getTravelTime());
        Assert.assertEquals("Office return should be: ", 4, result.getTimeOfArrivalAtDestination().longValue());
    }

    private ITravelTimeMatrix createTravelTimeMatrix(ILocation origin, ILocation destination, Collection<ILocation> locations) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        travelTimeMatrix.addUndirectedConnection(origin, destination, 50);

        for (ILocation locationA : locations) {
            travelTimeMatrix.addUndirectedConnection(origin, locationA, 2);
            travelTimeMatrix.addUndirectedConnection(destination, locationA, 10);
            for (ILocation locationB : locations)
                if (locationA != locationB) {
                    travelTimeMatrix.addUndirectedConnection(locationA, locationB, 1);
                }
        }
        return travelTimeMatrix;
    }

    private List<ITask> createTasksTwoWithSame() {
        TestTask taskA = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask taskB = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(1), "2");
        TestTask taskC = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(1), "3");
        TestTask taskD = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(3), "4");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }

    private List<ITask> createTasksAllSame() {
        TestTask taskA = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask taskB = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "2");
        TestTask taskC = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "3");
        TestTask taskD = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "4");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }

    private List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation("1"));
        locations.add(new TestLocation("2"));
        locations.add(new TestLocation("3"));
        locations.add(new TestLocation("4"));
        return locations;
    }
}