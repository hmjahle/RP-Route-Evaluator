package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.constraints.OvertimeConstraint;
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
import java.util.List;

/**
 * Tests if the max late arrival time window constraint is implemented correctly.
 */
public class ActivateAndDeactivateConstraintsTest extends JUnitTestAbstract {

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
        shift = new TestShift(0, 100);
    }

    @Test
    public void twoTaskInfeasible() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(0));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(34));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNull("Must be infeasible. ", result);

        boolean deactivated = routeEvaluator.deactivateConstraint(TimeWindowLateArrivalConstraint.class.getSimpleName());
        Assert.assertTrue("Constraint must be deactivated", deactivated);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNotNull("Must be feasible. ", result);

        boolean activated = routeEvaluator.activateConstraint(TimeWindowLateArrivalConstraint.class.getSimpleName());
        Assert.assertTrue("Constraint must be activated", activated);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void mustBecomeFeasibleAfterDeactivating() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint(10));
        routeEvaluator.addConstraint(new OvertimeConstraint());
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNull("Must be infeasible due to both constraints. ", result);

        routeEvaluator.deactivateConstraint(TimeWindowLateArrivalConstraint.class.getSimpleName());
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNull("Must be infeasible due to Overtime constraint. ", result);

        routeEvaluator.deactivateConstraint(OvertimeConstraint.class.getSimpleName());
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNotNull("Must be feasible due to both constraints are deactivated. ", result);
    }

    @Test
    public void mustBecomeInfeasibleAfterActivating() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addConstraint(new TimeWindowLateArrivalConstraint());
        routeEvaluator.addConstraint(new OvertimeConstraint());

        Assert.assertTrue("Must successfully deactivate.", routeEvaluator.deactivateConstraint(TimeWindowLateArrivalConstraint.class.getSimpleName()));
        Assert.assertTrue("Must successfully deactivate.", routeEvaluator.deactivateConstraint(OvertimeConstraint.class.getSimpleName()));

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNotNull("Must be feasible due to both constraints are deactivated. ", result);

        Assert.assertTrue("Must successfully activate.", routeEvaluator.activateConstraint(TimeWindowLateArrivalConstraint.class.getSimpleName()));
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNotNull("Must be still be feasible as constraint is feasible. ", result);

        Assert.assertTrue("Must successfully activate.", routeEvaluator.activateConstraint(OvertimeConstraint.class.getSimpleName()));
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertNull("Must be infeasible due to overtime constraint. ", result);
    }


}
