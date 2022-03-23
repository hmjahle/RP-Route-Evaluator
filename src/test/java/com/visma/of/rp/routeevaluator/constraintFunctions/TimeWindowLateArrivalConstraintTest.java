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
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.HashMap;
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
        allTasks = createTasksTimeWindows(locations);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(0, 150);

    }

    @Test
    public void oneStrictTaskFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(0));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
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
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    private RouteEvaluatorResult<ITask> findRoute(int maxLateArrival) {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(0));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(maxLateArrival));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
    }

    private RouteEvaluatorResult<ITask> getRoute(int distanceFromOffice, int maximumLateArrival) {
        travelTimeMatrix.addUndirectedConnection(office, locations.get(0), distanceFromOffice);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(maximumLateArrival));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, new HashMap<>(), shift);
    }
}
