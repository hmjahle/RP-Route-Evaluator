package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.objectives.SyncedTaskStartTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TravelTimeObjectiveFunction;
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

public class InsertTaskIntoRouteSyncedTasksTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    RouteEvaluator<ITask> routeEvaluator;
    IShift shift;
    Map<ITask, Integer> syncedTaskStartTimes;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(0, 100);
        routeEvaluator = createRouteEvaluator();
        syncedTaskStartTimes = createSyncedTaskStartTime();
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
    }


    @Test
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(3, 4));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTaskObjective(tasks, allTasks.get(2), syncedTaskStartTimes, shift);
        Assert.assertEquals("Objectives must be equal: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void oneTaskBothSynced() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(2, 3));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "3", result.getVisitSolution().get(1).getTask().getId());

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTaskObjective(tasks, allTasks.get(0), syncedTaskStartTimes, shift);
        Assert.assertEquals("Objectives must be equal: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void allTasksInsertFirst() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(1, 4));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTaskObjective(tasks, allTasks.get(0), syncedTaskStartTimes, shift);
        Assert.assertEquals("Objectives must be equal: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void allTasksInsertMiddle() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(1));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(2), syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "4", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "2", result.getVisitSolution().get(3).getTask().getId());

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTaskObjective(tasks, allTasks.get(2), syncedTaskStartTimes, shift);
        Assert.assertEquals("Objectives must be equal: ", result.getObjectiveValue(), objective, 1E-6);

    }

    @Test
    public void allTasksInsertLast() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 3));
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(3), syncedTaskStartTimes, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "2", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "3", result.getVisitSolution().get(3).getTask().getId());

        Double objective = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTaskObjective(tasks, allTasks.get(3), syncedTaskStartTimes, shift);
        Assert.assertEquals("Objectives must be equal: ", result.getObjectiveValue(), objective, 1E-6);
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

    private RouteEvaluator<ITask> createRouteEvaluator() {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());
        return routeEvaluator;
    }


    private Map<ITask, Integer> createSyncedTaskStartTime() {
        Map<ITask, Integer> syncedTaskStartTimes = new HashMap<>();
        syncedTaskStartTimes.put(allTasks.get(0), 5);
        syncedTaskStartTimes.put(allTasks.get(2), 35);
        return syncedTaskStartTimes;
    }
}