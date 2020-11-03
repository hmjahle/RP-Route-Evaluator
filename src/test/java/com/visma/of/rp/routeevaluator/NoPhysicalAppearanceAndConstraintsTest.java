package com.visma.of.rp.routeevaluator;


import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
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
import java.util.List;

import static benchmarking.benchmarking.printResult;

/**
 * Tests if the order of non-physical appearance tasks is handled correctly.
 */
public class NoPhysicalAppearanceAndConstraintsTest extends JUnitTestAbstract {

    List<ILocation> locations;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    ILocation office;
    IShift shift;

    @Before
    public void init() {
        office = createOffice();
        locations = createLocations();
        allTasks = createTaskList();
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(100, 0, 100);
    }

    @Test
    public void overtimeConstraintWithNoAppearanceAtEnd() {
        TestTask task1 = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 100, false, false, false, 0, 0, locations.get(1), "2");
        allTasks.add(task1);
        allTasks.add(task2);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 10);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task1.getLocation(), 1);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 100);

        RouteEvaluatorResult result = evaluateRoute(allTasks);

        Assert.assertNotNull("Must be feasible. ",result);
        Assert.assertEquals(10, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("Must return to office at: ", 22, result.getTimeOfOfficeReturn().longValue());
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
        return locations;
    }

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }

}
