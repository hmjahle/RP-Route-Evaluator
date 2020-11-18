package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.info.RouteEvaluationInfoAbstract;
import com.visma.of.rp.routeevaluator.evaluation.objectives.CustomCriteriaObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.SyncedTaskStartTimeObjectiveFunction;
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
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.*;

import static benchmarking.benchmarking.printResult;


/**
 * The route evaluator functionality where a route is split based on a custom criteria (function for a task).
 * There after a new route is returned. The test, tests this functionality for a criteria and at the same time the
 * opposite criteria.
 */
public class EvaluateByOrderOfTaskReInsertBasedOnCriteriaTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    IShift shift;
    RouteEvaluator routeEvaluator;
    Map<ITask, Long> syncedTasksStartTime;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(100, 0, 100);
        routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, office);
        routeEvaluator.addObjectiveIntraShift(new CustomCriteriaObjectiveFunction(
                RouteEvaluationInfoAbstract::isDestination, x -> (double) x.getStartOfServiceNextTask()));
        syncedTasksStartTime = createSyncedTaskStartTimes();
    }

    /**
     * A criteria that no tasks passes, hence nothing should happen compared to the standard evaluation.
     */
    @Test
    public void allTasksNoDifference() {

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, syncedTasksStartTime, shift);
        assertAllTasks(result);

        result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                allTasks, syncedTasksStartTime, shift, i -> i.isSynced() && i.isStrict());
        assertAllTasks(result);
    }

    /**
     * Tests if the solution is improved by re inserting all strict and synced tasks.
     * First splitting by "is strict" then opposite by "not strict".
     * The two ways should be equal as the criteria splits all tasks in two lists and merges them.
     */
    @Test
    public void fourTasksCriteriaStrictAndSynced() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(1));

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                tasks, syncedTasksStartTime, shift, i -> i.isSynced() || i.isStrict());
        assertStrictAndSynced(result);

        result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                tasks, syncedTasksStartTime, shift, i -> !(i.isSynced() || i.isStrict()));
        assertStrictAndSynced(result);
    }

    /**
     * Tests if the solution is improved by re inserting all strict tasks.
     * First splitting by "is strict" then opposite by "not strict".
     * The two ways should be equal as the criteria splits all tasks in two lists and merges them.
     */
    @Test
    public void allTasksCriteriaStrict() {

        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                allTasks, syncedTasksStartTime, shift, ITask::isStrict);
        assertAllStrict(result);

        result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                allTasks, syncedTasksStartTime, shift, i -> !i.isStrict());
        assertAllStrict(result);
    }


    @Test
    public void twoTasksCriteriaSynced() {

        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(0));

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                tasks, syncedTasksStartTime, shift, ITask::isSynced);
        printResult(result);
        assertTwoTasksSynced(result);

        result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                tasks, syncedTasksStartTime, shift, i -> !i.isSynced());
        printResult(result);
        assertTwoTasksSynced(result);
    }


    @Test
    public void twoTasksCriteriaStrict() {

        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(1));

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                tasks, syncedTasksStartTime, shift, ITask::isStrict);
        assertStrictTwoTasks(result);

        result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                tasks, syncedTasksStartTime, shift, i -> !i.isStrict());
        assertStrictTwoTasks(result);
    }


    @Test
    public void allTasksCriteriaStrictAndSynced() {
        routeEvaluator.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction());

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                allTasks, syncedTasksStartTime, shift, i -> i.isSynced() || i.isStrict());
        assertAllStrictAndSynced(result);

        result = routeEvaluator.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(
                allTasks, syncedTasksStartTime, shift, i -> !(i.isSynced() || i.isStrict()));
        assertAllStrictAndSynced(result);
    }


    private void assertStrictTwoTasks(RouteEvaluatorResult result) {
        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());

        Assert.assertEquals("Start time visit 1 should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertTrue("Task type 1 should be strict: ", result.getVisitSolution().get(0).getTask().isStrict());

        Assert.assertEquals("Start time visit 2 should be: ", 40, result.getVisitSolution().get(1).getStartTime());

        Assert.assertEquals("Office return should be: ", 47, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 47, result.getObjectiveValue(), 1E-6);
    }


    private void assertAllStrict(RouteEvaluatorResult result) {
        Assert.assertEquals("Number of visits should be: ", 8, result.getVisitSolution().size());

        Assert.assertEquals("Start time visit 1 should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertTrue("Task type visit 1 should be strict: ", result.getVisitSolution().get(0).getTask().isStrict());

        Assert.assertEquals("Start time visit 2 should be: ", 26, result.getVisitSolution().get(1).getStartTime());

        Assert.assertEquals("Start time visit 3 should be: ", 40, result.getVisitSolution().get(2).getStartTime());

        Assert.assertEquals("Start time visit 4 should be: ", 46, result.getVisitSolution().get(3).getStartTime());

        Assert.assertEquals("Start time visit 5 should be: ", 55, result.getVisitSolution().get(4).getStartTime());
        Assert.assertTrue("Task type visit 5 should be strict: ", result.getVisitSolution().get(4).getTask().isStrict());

        Assert.assertEquals("Start time visit 6 should be: ", 61, result.getVisitSolution().get(5).getStartTime());

        Assert.assertEquals("Start time visit 7 should be: ", 67, result.getVisitSolution().get(6).getStartTime());

        Assert.assertEquals("Start time visit 8 should be: ", 73, result.getVisitSolution().get(7).getStartTime());

        Assert.assertEquals("Office return should be: ", 80, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 82, result.getObjectiveValue(), 1E-6);
    }


    private void assertAllStrictAndSynced(RouteEvaluatorResult result) {
        Assert.assertEquals("Number of visits should be: ", 8, result.getVisitSolution().size());

        Assert.assertEquals("Start time visit 1 should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertTrue("Task type visit 1 should be synced: ", result.getVisitSolution().get(0).getTask().isSynced());

        Assert.assertEquals("Start time visit 2 should be: ", 26, result.getVisitSolution().get(1).getStartTime());
        Assert.assertTrue("Task type visit 2 should be strict: ", result.getVisitSolution().get(1).getTask().isStrict());

        Assert.assertEquals("Start time visit 3 should be: ", 40, result.getVisitSolution().get(2).getStartTime());

        Assert.assertEquals("Start time visit 4 should be: ", 46, result.getVisitSolution().get(3).getStartTime());

        Assert.assertEquals("Start time visit 5 should be: ", 55, result.getVisitSolution().get(4).getStartTime());
        Assert.assertTrue("Task type visit 5 should be synced: ", result.getVisitSolution().get(4).getTask().isSynced());

        Assert.assertEquals("Start time visit 6 should be: ", 61, result.getVisitSolution().get(5).getStartTime());

        Assert.assertEquals("Start time visit 7 should be: ", 67, result.getVisitSolution().get(6).getStartTime());
        Assert.assertTrue("Task type visit 7 should be synced: ", result.getVisitSolution().get(6).getTask().isSynced());

        Assert.assertEquals("Start time visit 8 should be: ", 73, result.getVisitSolution().get(7).getStartTime());
        Assert.assertTrue("Task type visit 8 should be strict: ", result.getVisitSolution().get(7).getTask().isStrict());

        Assert.assertEquals("Office return should be: ", 80, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 80, result.getObjectiveValue(), 1E-6);
    }


    private void assertTwoTasksSynced(RouteEvaluatorResult result) {
        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());

        Assert.assertEquals("Start time visit 1 should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertTrue("Task type visit 1 should be synced: ", result.getVisitSolution().get(0).getTask().isSynced());

        Assert.assertEquals("Start time visit 2 should be: ", 40, result.getVisitSolution().get(1).getStartTime());

        Assert.assertEquals("Office return should be: ", 47, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 47, result.getObjectiveValue(), 1E-6);
    }


    private void assertAllTasks(RouteEvaluatorResult result) {
        Assert.assertEquals("Number of visits should be: ", 8, result.getVisitSolution().size());

        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertTrue("Task type should be synced: ", result.getVisitSolution().get(0).getTask().isSynced());

        Assert.assertEquals("Start time should be: ", 26, result.getVisitSolution().get(1).getStartTime());
        Assert.assertTrue("Task type should be strict: ", result.getVisitSolution().get(1).getTask().isStrict());

        Assert.assertEquals("Start time should be: ", 40, result.getVisitSolution().get(2).getStartTime());
        Assert.assertEquals("Start time should be: ", 46, result.getVisitSolution().get(3).getStartTime());

        Assert.assertEquals("Start time should be: ", 55, result.getVisitSolution().get(4).getStartTime());
        Assert.assertTrue("Task type should be synced: ", result.getVisitSolution().get(4).getTask().isSynced());

        Assert.assertEquals("Start time should be: ", 67, result.getVisitSolution().get(5).getStartTime());
        Assert.assertTrue("Task type should be synced: ", result.getVisitSolution().get(5).getTask().isSynced());

        Assert.assertEquals("Start time should be: ", 73, result.getVisitSolution().get(6).getStartTime());
        Assert.assertTrue("Task type should be strict:: ", result.getVisitSolution().get(6).getTask().isStrict());

        Assert.assertEquals("Start time should be: ", 79, result.getVisitSolution().get(7).getStartTime());

        Assert.assertEquals("Office return should be: ", 86, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 86, result.getObjectiveValue(), 1E-6);
    }


    private void assertStrictAndSynced(RouteEvaluatorResult result) {
        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("Start time visit 1 should be: ", 20, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Start time visit 2 should be: ", 26, result.getVisitSolution().get(1).getStartTime());
        Assert.assertEquals("Start time visit 3 should be: ", 40, result.getVisitSolution().get(2).getStartTime());
        Assert.assertEquals("Start time visit 3 should be: ", 46, result.getVisitSolution().get(3).getStartTime());
        Assert.assertEquals("Office return should be: ", 53, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 53, result.getObjectiveValue(), 1E-6);
    }


    private Map<ITask, Long> createSyncedTaskStartTimes() {
        Map<ITask, Long> syncedTasksStartTime = new HashMap<>();
        syncedTasksStartTime.put(allTasks.get(0), 20L);
        syncedTasksStartTime.put(allTasks.get(4), 55L);
        syncedTasksStartTime.put(allTasks.get(5), 67L);
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
        ITask taskA = createSyncedTask(5, 15, 50);
        ITask taskB = createStrictTask(5, 20, 30);
        ITask taskC = createStandardTask(5, 40, 70);
        ITask taskD = createStandardTask(5, 45, 80);
        ITask taskE = createSyncedTask(5, 50, 90);
        ITask taskF = createSyncedTask(5, 30, 70);
        ITask taskG = createStrictTask(5, 55, 60);
        ITask taskH = createStandardTask(5, 50, 80);

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
