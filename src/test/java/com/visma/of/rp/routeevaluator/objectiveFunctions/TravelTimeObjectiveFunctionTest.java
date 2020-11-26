package com.visma.of.rp.routeevaluator.objectiveFunctions;

import com.visma.of.rp.routeevaluator.evaluation.objectives.TravelTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTask;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.*;
import java.util.stream.Collectors;

public class TravelTimeObjectiveFunctionTest extends JUnitTestAbstract {

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
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        RouteEvaluatorResult result = evaluateRoute( tasks, null);
        Assert.assertEquals("Cost should be: ", 4, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void fiveTaskNonSynced() {
        List<ITask> tasks = allTasks.stream().filter(i -> !i.isSynced()).collect(Collectors.toList());

        RouteEvaluatorResult result = evaluateRoute( tasks, null);

        Assert.assertEquals("Cost should be: ", 8, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void threeSynced() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(5));

        Map<ITask, Long> syncedTasksStartTime = getSyncedStartTime();
        RouteEvaluatorResult result = evaluateRoute(tasks, syncedTasksStartTime);

        Assert.assertEquals("Cost should be: ", 6, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void nonPhysicalAppearance() {
        List<ITask> tasks = new ArrayList<>();
        TestTask taskA = (TestTask) allTasks.get(2);
        TestTask taskNonPhys = new TestTask(1, 40, 50, false, false, false, 0, 0, allTasks.get(0).getLocation(), "non-phys");
        allTasks.add(taskNonPhys);
        TestTask taskC = (TestTask) allTasks.get(3);
        tasks.add(taskA);
        tasks.add(taskNonPhys);
        tasks.add(taskC);

        RouteEvaluatorResult result = evaluateRoute(tasks, null);

        Assert.assertEquals("Cost should be: ", 5, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void allTasks() {
        Map<ITask, Long> syncedTasksStartTime = getSyncedStartTime();
        RouteEvaluatorResult result = evaluateRoute(allTasks, syncedTasksStartTime);

        Assert.assertEquals("Cost should be: ", 11, result.getObjectiveValue(), 1E-6);
    }


    @Test
    public void standardTasksWithRobustness() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(7));
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));
        travelTimeMatrix.addUndirectedConnection(office, allTasks.get(7).getLocation(), 10);
        RouteEvaluatorResult result = evaluateRoute( tasks, null);

        Assert.assertEquals("Cost should be: ", 14, result.getObjectiveValue(), 1E-6);
    }

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks, Map<ITask, Long> syncedTasksStartTime) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, syncedTasksStartTime, shift);
    }

    private Map<ITask, Long> getSyncedStartTime() {
        Map<ITask, Long> syncedTasksStartTime = new HashMap<>();
        syncedTasksStartTime.put(allTasks.get(0), 20L);
        syncedTasksStartTime.put(allTasks.get(4), 60L);
        syncedTasksStartTime.put(allTasks.get(5), 80L);
        return syncedTasksStartTime;
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