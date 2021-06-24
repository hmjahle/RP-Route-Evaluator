package com.visma.of.rp.routeevaluator.evaluateRoutes;

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

import java.util.*;

public class WalkingWithConnectionsRemovedTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    IShift shift;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(0, 100);
    }

    @Test
    public void returnToOfficeLastTaskNotConnectedToOffice() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        travelTimeMatrix.removeDirectedConnection(allTasks.get(1).getLocation(), office);
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertFalse("Should not be connected back to office.", travelTimeMatrix.connected(allTasks.get(1).getLocation(), office));
        Assert.assertNull("Should be infeasible.", result);
    }


    @Test
    public void returnToOfficeFirstTaskNotConnectedToOffice() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        travelTimeMatrix.removeDirectedConnection(allTasks.get(0).getLocation(), office);
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertFalse("Should not be connected back to office.", travelTimeMatrix.connected(allTasks.get(0).getLocation(), office));
        Assert.assertNotNull("Should be feasible.", result);
        Assert.assertEquals("Should return at.", 23, result.getTimeOfArrivalAtDestination().intValue());
    }


    @Test
    public void notConnectFromOffice() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        travelTimeMatrix.removeDirectedConnection(office, allTasks.get(0).getLocation());
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertFalse("Should not be connect from office.", travelTimeMatrix.connected(office, allTasks.get(0).getLocation()));
        Assert.assertNull("Should be infeasible.", result);
    }

    @Test
    public void twoTasksNotConnected() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        travelTimeMatrix.removeDirectedConnection(allTasks.get(0).getLocation(), allTasks.get(1).getLocation());
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertFalse("Tasks should not be connected.", travelTimeMatrix.connected(allTasks.get(0).getLocation(), allTasks.get(1).getLocation()));
        Assert.assertNull("Should be infeasible.", result);
    }

    @Test
    public void insertTasksFirstPosition() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        travelTimeMatrix.removeDirectedConnection(allTasks.get(1).getLocation(), allTasks.get(3).getLocation());
        travelTimeMatrix.removeDirectedConnection(allTasks.get(2).getLocation(), allTasks.get(3).getLocation());
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<ITask>(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(3), shift);

        Assert.assertFalse("Tasks should not be connected.", travelTimeMatrix.connected(allTasks.get(1).getLocation(), allTasks.get(3).getLocation()));
        Assert.assertFalse("Tasks should not be connected.", travelTimeMatrix.connected(allTasks.get(2).getLocation(), allTasks.get(3).getLocation()));
        Assert.assertNotNull("Should be feasible.", result);
        Assert.assertEquals("Should be third task.", allTasks.get(2).getId(), result.getRoute().extractEmployeeTasks().get(2).getId());
        Assert.assertEquals("Should be second task.", allTasks.get(1).getId(), result.getRoute().extractEmployeeTasks().get(1).getId());
        Assert.assertEquals("Should be first task.", allTasks.get(3).getId(), result.getRoute().extractEmployeeTasks().get(0).getId());
    }


    @Test
    public void insertTasksSecondPosition() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(2));
        travelTimeMatrix.removeDirectedConnection(allTasks.get(3).getLocation(), office);
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(3), shift);

        Assert.assertFalse("Should not be connected back to office.", travelTimeMatrix.connected(allTasks.get(3).getLocation(), office));
        Assert.assertNotNull("Should be feasible.", result);
        Assert.assertEquals("Should be first task.", allTasks.get(1).getId(), result.getRoute().extractEmployeeTasks().get(0).getId());
        Assert.assertEquals("Should be second task.", allTasks.get(3).getId(), result.getRoute().extractEmployeeTasks().get(1).getId());
        Assert.assertEquals("Should be third task.", allTasks.get(2).getId(), result.getRoute().extractEmployeeTasks().get(2).getId());
    }

    private TestTravelTimeMatrix createTravelTimeMatrix(ILocation office, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, taskA.getLocation(), 2);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 1);
        }
        return travelTimeMatrix;
    }

    private List<ITask> createTasks() {
        ITask taskA = createStandardTask(1, 10, 50,"0");
        ITask taskB = createStandardTask(1, 20, 60,"1");
        ITask taskC = createStandardTask(1, 21, 70,"2");
        ITask taskD = createStandardTask(1, 22, 80,"3");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }
}