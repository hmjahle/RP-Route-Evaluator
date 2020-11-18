package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.objectives.TimeWindowObjectiveFunction;
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

import java.util.*;

public class InsertMultipleTasksIntoRouteSyncedTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    RouteEvaluator routeEvaluator;
    IShift shift;
    Map<ITask, Long> syncedTaskStartTimes;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(100, 0, 100);
        syncedTaskStartTimes = createSyncedTaskStartTime();
        routeEvaluator = createRouteEvaluator();
    }

    @Test
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(3, 4));
        List<ITask> insertTasks = new ArrayList<>(allTasks.subList(0, 1));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTasks(tasks, insertTasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());
    }

    @Test
    public void allTasksInsertInterlaced() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(3));
        List<ITask> insertTasks = new ArrayList<>();
        insertTasks.add(allTasks.get(0));
        insertTasks.add(allTasks.get(2));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTasks(tasks, insertTasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "2", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());
    }

    @Test
    public void allTasksInsertMiddle() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(3));
        List<ITask> insertTasks = new ArrayList<>(allTasks.subList(1, 2));

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTasks(tasks, insertTasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 3, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "2", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "4", result.getVisitSolution().get(2).getTask().getId());
    }

    @Test
    public void allTasksInsertLastTasks() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 2));
        List<ITask> insertTasks = new ArrayList<>(allTasks.subList(2, 4));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTasks(tasks, insertTasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());
    }

    @Test
    public void allTasksInsertFirstTaskLast() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(1));
        tasks.add(allTasks.get(0));
        List<ITask> insertTasks = new ArrayList<>();
        insertTasks.add(allTasks.get(2));
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTasks(tasks, insertTasks, syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "3", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "2", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "1", result.getVisitSolution().get(3).getTask().getId());
    }

    private ITravelTimeMatrix createTravelTimeMatrix(ILocation office, Collection<ITask> tasks) {
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
        TestTask taskA = new TestTask(1, 0, 10, false, true, true, 0, 0, new TestLocation(false), "1");
        TestTask taskB = new TestTask(1, 10, 20, false, false, true, 0, 0, new TestLocation(false), "2");
        TestTask taskC = new TestTask(1, 20, 30, false, true, true, 0, 0, new TestLocation(false), "3");
        TestTask taskD = new TestTask(1, 30, 40, false, false, true, 0, 0, new TestLocation(false), "4");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }

    private RouteEvaluator createRouteEvaluator() {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        return routeEvaluator;
    }

    private Map<ITask, Long> createSyncedTaskStartTime() {
        Map<ITask, Long> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 19L);
        syncedTaskStartTimes.put(allTasks.get(2), 29L);
        return syncedTaskStartTimes;
    }
}