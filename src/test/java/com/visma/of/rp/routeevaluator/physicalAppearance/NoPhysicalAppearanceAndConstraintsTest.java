package com.visma.of.rp.routeevaluator.physicalAppearance;


import com.visma.of.rp.routeevaluator.evaluation.constraints.OvertimeConstraint;
import com.visma.of.rp.routeevaluator.evaluation.constraints.StrictTimeWindowConstraint;
import com.visma.of.rp.routeevaluator.evaluation.constraints.SyncedTasksConstraint;
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
import java.util.Map;

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
        allTasks = new ArrayList<>();
        travelTimeMatrix = new TestTravelTimeMatrix();
        shift = new TestShift(0, 100);
    }

    @Test
    public void overtimeConstraintWithNoAppearanceAtEndFeasible() {
        TestTask task1 = new TestTask(1, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 100, false, false, false, 0, 0, locations.get(1), "2");
        allTasks.add(task1);
        allTasks.add(task2);
        travelTimeMatrix = new TestTravelTimeMatrix(allTasks);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 10);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task1.getLocation(), 1);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 100);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new OvertimeConstraint());

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, new HashMap<>(), shift);
        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals(10, getVisitTravelTime(result, 0));
        Assert.assertEquals("No physical appearance should have no travel time.", 0, getVisitTravelTime(result, 1));
        Assert.assertEquals("Must return to office at: ", 22, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void overtimeConstraintWithNoAppearanceAtEndInfeasible() {
        TestTask task1 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(10, 0, 100, false, false, false, 0, 0, locations.get(1), "2");
        allTasks.add(task1);
        allTasks.add(task2);

        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 50);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task1.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 20);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new OvertimeConstraint());

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, new HashMap<>(), shift);

        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void strictTimeWindowConstraintWithAppearanceAtEndInfeasible() {
        TestTask task1 = new TestTask(1, 0, 100, true, false, false, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 50, true, false, true, 0, 0, locations.get(1), "2");
        allTasks.add(task1);
        allTasks.add(task2);

        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 20);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task1.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 50);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new StrictTimeWindowConstraint());
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, new HashMap<>(), shift);

        Assert.assertNull("Must be infeasible. ", result);
    }

    @Test
    public void strictTimeWindowConstraintWithNoAppearanceAtEndFeasible() {
        TestTask task1 = new TestTask(1, 0, 100, true, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(1, 0, 50, true, false, false, 0, 0, locations.get(1), "2");
        allTasks.add(task1);
        allTasks.add(task2);

        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 20);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task1.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 50);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new StrictTimeWindowConstraint());

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, new HashMap<>(), shift);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Must return to office at. ", 42, result.getTimeOfArrivalAtDestination(), DELTA);

    }

    @Test
    public void syncedTaskConstraintWithAppearanceAtStartFeasible() {
        TestTask task1 = new TestTask(5, 0, 100, false, false, false, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(5, 0, 100, false, true, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(5, 0, 100, false, true, true, 0, 0, locations.get(2), "3");
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);

        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 20);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(task1.getLocation(), task2.getLocation(), 25);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task3.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task3.getLocation(), 45);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new SyncedTasksConstraint());

        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(task2, 10);
        syncedTaskStartTimes.put(task3, 50);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);

        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Must return to office at. ", 100, result.getTimeOfArrivalAtDestination(), DELTA);

    }


    @Test
    public void syncedTaskConstraintWithAppearanceInMiddleInfeasible() {
        TestTask task1 = new TestTask(5, 0, 100, false, true, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(5, 0, 100, false, false, false, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(5, 0, 100, false, true, true, 0, 0, locations.get(2), "3");
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);

        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 20);
        travelTimeMatrix.addUndirectedConnection(task1.getLocation(), task2.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(task1.getLocation(), task3.getLocation(), 15);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task3.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task3.getLocation(), 20);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new SyncedTasksConstraint());

        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(task1, 10);
        syncedTaskStartTimes.put(task3, 40);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);
        Assert.assertNull("Must be infeasible. ", result);


    }

    @Test
    public void syncedTaskConstraintWithAppearanceAtEndFeasible() {
        TestTask task1 = new TestTask(5, 0, 50, false, true, false, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(5, 0, 100, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(5, 0, 100, false, true, true, 0, 0, locations.get(2), "3");

        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);

        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 10);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task3.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task3.getLocation(), 45);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 1000);

        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new SyncedTasksConstraint());
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(task1, 40);
        syncedTaskStartTimes.put(task3, 60);

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);
        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Must return to office at. ", 110, result.getTimeOfArrivalAtDestination(), DELTA);

    }


    @Test
    public void syncedTaskConstraintWithoutAppearanceFeasible() {
        TestTask task1 = new TestTask(5, 0, 100, false, true, false, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(5, 0, 100, false, true, false, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(5, 0, 100, false, true, false, 0, 0, locations.get(2), "3");
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);

        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 20);
        travelTimeMatrix.addUndirectedConnection(office, task2.getLocation(), 5);
        travelTimeMatrix.addUndirectedConnection(office, task3.getLocation(), 45);
        travelTimeMatrix.addUndirectedConnection(task1.getLocation(), task2.getLocation(), 25);
        travelTimeMatrix.addUndirectedConnection(task1.getLocation(), task3.getLocation(), 25);
        travelTimeMatrix.addUndirectedConnection(task2.getLocation(), task3.getLocation(), 5);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addConstraint(new SyncedTasksConstraint());

        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(task1, 10);
        syncedTaskStartTimes.put(task2, 15);
        syncedTaskStartTimes.put(task3, 20);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTaskStartTimes, shift);
        Assert.assertNotNull("Must be feasible. ", result);
        Assert.assertEquals("Must return to office at. ", 25, result.getTimeOfArrivalAtDestination(), DELTA);

    }

    protected List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

}
