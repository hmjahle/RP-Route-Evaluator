package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.objectives.TravelTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
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

/**
 * Test the RouteEvaluator function of finding the objective value when removing a task by its index in a route.
 */
public class RemoveTasksFromRouteTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    RouteEvaluator routeEvaluator;
    IShift shift;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(0, 100);
        routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
    }

    /**
     * Skip when there is one task, should give an empty route, i.e., 0 objective in this case.
     */
    @Test
    public void skipOneTask() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 1));
        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks, 0, null, shift);
        Assert.assertNotNull("Must be feasible.", objective);
        Assert.assertEquals("Objective must be 0, no tasks left in route.", 0, objective, 1E-6);
    }

    /**
     * Route is O -> 1 -> D (distance == 4)
     */
    @Test
    public void skipFirstTaskOfTwo() {
        List<ITask> task1 = new ArrayList<>(allTasks.subList(1, 2));
        List<ITask> tasks2 = new ArrayList<>(allTasks.subList(0, 2));

        Double objective1 = routeEvaluator.evaluateRouteObjective(task1, shift);
        Double objective2 = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks2, 0, null, shift);

        Assert.assertEquals("Objective must be equal travel time 4.", 4, objective1, 1E-6);
        Assert.assertEquals("Objectives must be equal", objective1, objective2, 1E-6);
    }

    /**
     * Final route: O -> 0 -> D (distance == 4), original route :O -> 0 -> 1 -> D (distance == 14)
     */
    @Test
    public void skipSecondTaskOfTwo() {
        List<ITask> task1 = new ArrayList<>(allTasks.subList(0, 1));
        List<ITask> tasks2 = new ArrayList<>(allTasks.subList(0, 2));

        Double objective1 = routeEvaluator.evaluateRouteObjective(task1, shift);
        Double objective2 = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks2, 1, null, shift);

        Assert.assertEquals("Objective must be equal travel time 4.", 4, objective1, 1E-6);
        Assert.assertEquals("Objectives must be equal", objective1, objective2, 1E-6);
    }

    /**
     * Final route: O -> 0 -> 2 -> D (distance == 14), original route: O -> 0 -> 1 -> 2 -> D (distance == 34)
     */
    @Test
    public void skipTaskInTheMiddle() {
        List<ITask> tasks1 = new ArrayList<>();
        tasks1.add(allTasks.get(0));
        tasks1.add(allTasks.get(2));
        List<ITask> tasks2 = new ArrayList<>(allTasks.subList(0, 3));

        Double objective1 = routeEvaluator.evaluateRouteObjective(tasks1, shift);
        Double objective2 = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks2, 1, null, shift);

        Assert.assertEquals("Objective must be equal travel time 14.", 14, objective1, 1E-6);
        Assert.assertEquals("Objectives must be equal", objective1, objective2, 1E-6);
    }

    /**
     * Skips all tasks and must therefore be feasible but have a zero objective.
     */
    @Test
    public void skipAllTasks() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 3));
        List<Integer> skipIndices = new ArrayList<>();
        skipIndices.add(0);
        skipIndices.add(1);
        skipIndices.add(2);

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks, skipIndices, null, shift);

        Assert.assertNotNull("Must be feasible.", objective);
        Assert.assertEquals("Objective must be 0, no tasks left in route.", 0, objective, 1E-6);
    }

    /**
     * Final route: O -> 0 -> 2 -> D (distance == 4), original route: O -> 0 -> 1 -> 2 -> D (distance == 34)
     */
    @Test
    public void skipMiddleTask() {

        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 3));
        List<Integer> skipIndices = new ArrayList<>();
        skipIndices.add(1);

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks, skipIndices, null, shift);

        Assert.assertNotNull("Must be feasible.", objective);
        Assert.assertEquals("Objective must be 14.", 14, objective, 1E-6);
    }

    /**
     * Final route: O -> 1 -> D (distance == 4), original route: O -> 0 -> 1 -> 2 -> D (distance == 34)
     */
    @Test
    public void skipFirstAndLastTask() {
        List<ITask> tasks1 = new ArrayList<>();
        tasks1.add(allTasks.get(1));

        List<ITask> tasks2 = new ArrayList<>(allTasks.subList(0, 3));
        List<Integer> skipIndices = new ArrayList<>();
        skipIndices.add(0);
        skipIndices.add(2);
        
        Double objective1 = routeEvaluator.evaluateRouteObjective(tasks1, shift);
        Double objective2 = routeEvaluator.evaluateRouteByTheOrderOfTasksRemoveTaskObjective(tasks2, skipIndices, null, shift);

        Assert.assertEquals("Objective must be 4.", 4, objective2, 1E-6);
        Assert.assertEquals("Objectives must be equal", objective1, objective2, 1E-6);
    }


    private ITravelTimeMatrix createTravelTimeMatrix(ILocation office, List<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix(tasks);
        for (ITask task : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, task.getLocation(), 2);
        }
        travelTimeMatrix.addUndirectedConnection(tasks.get(0).getLocation(), tasks.get(1).getLocation(), 10);
        travelTimeMatrix.addUndirectedConnection(tasks.get(0).getLocation(), tasks.get(2).getLocation(), 10);
        travelTimeMatrix.addUndirectedConnection(tasks.get(1).getLocation(), tasks.get(2).getLocation(), 20);

        return travelTimeMatrix;
    }

    private List<ITask> createTasks() {
        TestTask taskA = new TestTask(1, 0, 10, false, false, true, 0, 0, new TestLocation(false), "1");
        TestTask taskB = new TestTask(1, 10, 20, false, false, true, 0, 0, new TestLocation(false), "2");
        TestTask taskC = new TestTask(1, 20, 30, false, false, true, 0, 0, new TestLocation(false), "3");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        return tasks;
    }

}