package com.visma.of.rp.routeevaluator;


import com.visma.of.rp.routeevaluator.constraints.OvertimeConstraint;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests if the overtime constraint is implemented correctly.
 */
public class OvertimeTaskConstraintTest extends JUnitTestAbstract {

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
    public void oneTaskFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        RouteEvaluatorResult result = evaluateRoute(tasks);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void oneTaskInfeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(createStandardTask(10, 90, 100));
        travelTimeMatrix.addUndirectedConnection(office, tasks.get(0).getLocation(), 5);
        RouteEvaluatorResult result = evaluateRoute(tasks);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void fiveTasksFeasible() {
        RouteEvaluatorResult result = evaluateRoute(allTasks);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    /**
     * Returns 1 minute late.
     */
    @Test
    public void sixTasksInfeasible() {
        List<ITask> tasks = new ArrayList<>();
        ITask task6 = createStandardTask(10, 34, 100);
        locations.add(task6.getLocation());
        tasks.add(task6);
        tasks.addAll(allTasks);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        RouteEvaluatorResult result = evaluateRoute(tasks);
        Assert.assertNull("Must be infeasible. ", result);
    }

    /**
     * Returns exactly on time.
     */
    @Test
    public void sixTasksFeasible() {
        List<ITask> tasks = new ArrayList<>();
        ITask task6 = createStandardTask(10, 33, 100);
        locations.add(task6.getLocation());
        tasks.add(task6);
        tasks.addAll(allTasks);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        RouteEvaluatorResult result = evaluateRoute(tasks);

        Assert.assertNotNull("Must be feasible. ", result);
    }


    private List<ITask> createTasks(List<ILocation> locations) {
        TestTask task1 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(2), "3");
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

    private TestTravelTimeMatrix createTravelTimeMatrix(Collection<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ILocation locationA : locations) {
            travelTimeMatrix.addUndirectedConnection(office, locationA, 2);
            for (ILocation locationB : locations)
                if (locationA != locationB)
                    travelTimeMatrix.addUndirectedConnection(locationA, locationB, 1);
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
        return locations;
    }

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new OvertimeConstraint());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }
}
