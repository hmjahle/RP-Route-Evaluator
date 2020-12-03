package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.results.Visit;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.*;
import java.util.stream.Collectors;

public class RouteEvaluatorResultTest extends JUnitTestAbstract {


    RouteEvaluatorResult result;
    List<ITask> allTasks;

    @Before
    public void initialize() {
        allTasks = createTasks();
        ILocation office = createOffice();
        ITravelTimeMatrix travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        IShift shift = new TestShift(0, 100);
        Map<ITask, Integer> syncedTasksStartTime = getSyncedStartTime(allTasks);
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, allTasks, office);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTasksStartTime, shift);
    }

    @Test
    public void normalTasks() {
        List<ITask> routeTasks = result.getRoute().extractEmployeeTasks();
        Assert.assertEquals("Should be 8 tasks. ", 8, routeTasks.size());
        for (int i = 0; i < allTasks.size(); i++)
            Assert.assertEquals("Task should be equal: ",
                    allTasks.get(i).getId(),
                    routeTasks.get(i).getId());
    }

    @Test
    public void syncedTasks() {

        Set<Visit> syncedVisits = result.getRoute().extractSyncedVisits();

        Assert.assertEquals("Must be 4 synced tasks. ", 4, syncedVisits.size());
        Set<String> taskIds = syncedVisits.stream().map(i -> i.getTask().getId()).collect(Collectors.toSet());
        Assert.assertTrue("All synced tasks must be in the set.", taskIds.containsAll(Arrays.asList("1", "4", "5", "6")));

        for (Visit visit : syncedVisits) {
            Assert.assertTrue("Task must be synced: ", visit.getTask().isSynced());
            Assert.assertTrue("Task must be contained: ", allTasks.contains(visit.getTask()));
        }

        for (Visit visit : result.getVisitSolution()) {
            if (visit.getTask().isSynced())
                Assert.assertTrue("Visit must be in the synced set: ", syncedVisits.contains(visit));
        }

    }

    @Test
    public void strictTasks() {

        Set<Visit> strictVisits = result.getRoute().extractStrictVisits();
        Set<String> taskIds = strictVisits.stream().map(i -> i.getTask().getId()).collect(Collectors.toSet());
        Assert.assertTrue("All synced tasks must be in the set.", taskIds.containsAll(Arrays.asList("2", "5", "7")));

        Assert.assertEquals("Should be 3 strict tasks. ", 3, strictVisits.size());
        for (Visit visit : strictVisits) {
            Assert.assertTrue("Task must be strict: ", visit.getTask().isStrict());
            Assert.assertTrue("Task must be contained: ", allTasks.contains(visit.getTask()));
        }

        for (Visit visit : result.getVisitSolution()) {
            if (visit.getTask().isStrict())
                Assert.assertTrue("Visit must be in the synced set: ", strictVisits.contains(visit));
        }
    }


    private Map<ITask, Integer> getSyncedStartTime(List<ITask> allTasks) {
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
        ITask taskA = createSyncedTask(1, 10, 50, "1");
        ITask taskB = createStrictTask(1, 20, 60, "2");
        ITask taskC = createStandardTask(1, 30, 70, "3");
        ITask taskD = createSyncedTask(1, 40, 80, "4");
        ITask taskE = createSyncedStrictTask(1, 50, 90, "5");
        ITask taskF = createSyncedTask(1, 60, 100, "6");
        ITask taskG = createStrictTask(1, 70, 110, "7");
        ITask taskH = createStandardTask(1, 80, 120, "8");

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