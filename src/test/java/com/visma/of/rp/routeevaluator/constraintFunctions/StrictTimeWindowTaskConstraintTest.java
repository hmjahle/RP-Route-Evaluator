package com.visma.of.rp.routeevaluator.constraintFunctions;

import com.visma.of.rp.routeevaluator.evaluation.constraints.StrictTimeWindowConstraint;
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
 * Tests if the strict time window constraint is implemented correctly.
 */
public class StrictTimeWindowTaskConstraintTest extends JUnitTestAbstract {

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
        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void twoStrictTaskInfeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(0));
        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void fiveMixedTasksFeasible() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks);
        Assert.assertNotNull("Must be feasible. ", result);
    }

    @Test
    public void fiveMixedTasksInfeasible() {
        travelTimeMatrix.addUndirectedConnection(locations.get(1), locations.get(2), 6);
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void nonStrictTasksBreakTimeWindowsFeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);
        Assert.assertNotNull("Must be feasible. ", result);
    }




    private RouteEvaluatorResult<ITask> evaluateRoute(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new StrictTimeWindowConstraint());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }
}
