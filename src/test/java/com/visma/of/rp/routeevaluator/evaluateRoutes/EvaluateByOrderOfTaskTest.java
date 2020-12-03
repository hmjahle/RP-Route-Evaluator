package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.*;
import java.util.stream.Collectors;

public class EvaluateByOrderOfTaskTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    IShift shift;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(0, 100);
    }

    @Test
    public void returnToOffice() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));

        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Must return at correct time!", 5, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));

        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Number of visits should be: ", 1, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Office return should be: ", 33, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
        double objective = routeEvaluator.evaluateRouteObjective(tasks, shift);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void fiveTaskNonSynced() {
        List<ITask> tasks = allTasks.stream().filter(i -> !i.isSynced()).collect(Collectors.toList());

        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Number of visits should be: ", 5, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(1).getStartTime());
        Assert.assertEquals("Start time should be: ", 40, result.getVisitSolution().get(2).getStartTime());
        Assert.assertEquals("Start time should be: ", 42, result.getVisitSolution().get(3).getStartTime());
        Assert.assertEquals("Start time should be: ", 44, result.getVisitSolution().get(4).getStartTime());
        Assert.assertEquals("Office return should be: ", 47, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
        double objective = routeEvaluator.evaluateRouteObjective(tasks, shift);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    @Test
    public void threeSynced() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(5));

        Map<ITask, Integer> syncedTasksStartTime = getSyncedStartTime();

        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTasksStartTime, shift);

        Assert.assertEquals("Number of visits should be: ", 3, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Start time should be: ", 60, result.getVisitSolution().get(1).getStartTime());
        Assert.assertEquals("Start time should be: ", 80, result.getVisitSolution().get(2).getStartTime());
        Assert.assertEquals("Office return should be: ", 83, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
        double objective = routeEvaluator.evaluateRouteObjective(tasks, syncedTasksStartTime, shift);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);

    }

    @Test
    public void allTasks() {
        Map<ITask, Integer> syncedTasksStartTime = getSyncedStartTime();

        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTasksStartTime, shift);

        Assert.assertEquals("Number of visits should be: ", 8, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Start time should be: ", 22, result.getVisitSolution().get(1).getStartTime());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(2).getStartTime());
        Assert.assertEquals("Start time should be: ", 40, result.getVisitSolution().get(3).getStartTime());
        Assert.assertEquals("Start time should be: ", 60, result.getVisitSolution().get(4).getStartTime());
        Assert.assertEquals("Start time should be: ", 80, result.getVisitSolution().get(5).getStartTime());
        Assert.assertEquals("Start time should be: ", 82, result.getVisitSolution().get(6).getStartTime());
        Assert.assertEquals("Start time should be: ", 84, result.getVisitSolution().get(7).getStartTime());
        Assert.assertEquals("Office return should be: ", 87, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
        double objective = routeEvaluator.evaluateRouteObjective(allTasks, syncedTasksStartTime, shift);
        Assert.assertEquals("Cost should be equal when evaluated alone: ", result.getObjectiveValue(), objective, 1E-6);
    }

    private Map<ITask, Integer> getSyncedStartTime() {
        Map<ITask, Integer> syncedTasksStartTime = new HashMap<>();
        syncedTasksStartTime.put(allTasks.get(0), 20);
        syncedTasksStartTime.put(allTasks.get(4), 60);
        syncedTasksStartTime.put(allTasks.get(5), 80);
        return syncedTasksStartTime;
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
        ITask taskA = createSyncedTask(1, 10, 50);
        ITask taskB = createStrictTask(1, 20, 60);
        ITask taskC = createStandardTask(1, 30, 70);
        ITask taskD = createStandardTask(1, 40, 80);
        ITask taskE = createSyncedTask(1, 50, 90);
        ITask taskF = createSyncedTask(1, 30, 70);
        ITask taskG = createStrictTask(1, 30, 70);
        ITask taskH = createStandardTask(1, 40, 80);

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        tasks.add(taskE);
        tasks.add(taskF);
        tasks.add(taskG);
        tasks.add(taskH);
        return tasks;
    }
}