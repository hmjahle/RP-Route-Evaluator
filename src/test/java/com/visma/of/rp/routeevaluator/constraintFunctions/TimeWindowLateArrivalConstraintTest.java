package com.visma.of.rp.routeevaluator.constraintFunctions;

import com.visma.of.rp.routeevaluator.evaluation.constraints.TimeWindowLateArrivalConstraint;
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
import java.util.Collection;
import java.util.List;

/**
 * Tests if the max late arrival time window constraint is implemented correctly.
 */
public class TimeWindowLateArrivalConstraintTest extends JUnitTestAbstract {

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
        shift = new TestShift(0, 150);

    }

    @Test
    public void oneStrictTaskFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(0));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void twoTaskInfeasible() {
        RouteEvaluatorResult<ITask> result = findRoute(34);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void twoTasksFeasible() {
        RouteEvaluatorResult<ITask> result = findRoute(35);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void fiveMixedTasksFeasibleNoSlack() {
        RouteEvaluatorResult<ITask> result = getRoute(10, 0);
        printRoute(result.getRoute());
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void fiveMixedTasksFeasibleWithDelayAndSlack() {
        RouteEvaluatorResult<ITask> result = getRoute(20, 10);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void fiveMixedTasksInfeasible() {
        RouteEvaluatorResult<ITask> result = getRoute(20, 5);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void feasibleWithDefaultValue() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint());
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        printRoute(result.getRoute());
        Assert.assertNotNull("Must be feasible. ", result);
    }

    private List<ITask> createTasks(List<ILocation> locations) {
        TestTask task1 = new TestTask(10, 0, 20, true, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(10, 30, 80, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(10, 0, 55, true, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(10, 70, 100, false, false, true, 0, 0, locations.get(3), "4");
        TestTask task5 = new TestTask(10, 80, 100, true, false, true, 0, 0, locations.get(4), "5");

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
            travelTimeMatrix.addUndirectedConnection(office, locationA, 10);
            for (ILocation locationB : locations)
                if (locationA != locationB)
                    travelTimeMatrix.addUndirectedConnection(locationA, locationB, 5);
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

    private RouteEvaluatorResult<ITask> findRoute(int maxLateArrival) {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(0));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(maxLateArrival));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }

    private RouteEvaluatorResult<ITask> getRoute(int distanceFromOffice, int maximumLateArrival) {
        travelTimeMatrix.addUndirectedConnection(office, locations.get(0), distanceFromOffice);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(maximumLateArrival));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
    }
}
