package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.objectives.OvertimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TimeWindowObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TravelTimeObjectiveFunction;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StartAtTaskTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    RouteEvaluator<ITask> routeEvaluator;
    IShift shift;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        shift = new TestShift(0, 100);
        routeEvaluator = createRouteEvaluator(allTasks);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
    }

    @Test
    public void insertOneTask() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(3, 4));
        routeEvaluator.useOpenEndedRoutes();

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), shift);
        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());

    }


    @Test
    public void emptyRoute() {
        List<ITask> tasks = new ArrayList<>();
        routeEvaluator.useOpenStartRoutes();
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
        Assert.assertEquals("Number of visits should be: ", 0, result.getVisitSolution().size());
    }

    @Test
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));

        routeEvaluator.useOpenStartRoutes();
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);

        Assert.assertEquals("Number of visits should be: ", 1, result.getVisitSolution().size());
        Assert.assertEquals("First task should be visited at time 0", 0, result.getVisitSolution().get(0).getStartTime());
    }

    @Test
    public void allTasksInsertFirst() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(1, 4));
        routeEvaluator.useOpenEndedRoutes();

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());


    }

    @Test
    public void allTasksInsertMiddleWithOpenStartAndEnd() {
        List<ITask> tasks = new ArrayList<>();

        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(3));

        routeEvaluator.useOpenStartRoutes();
        routeEvaluator.useOpenEndedRoutes();

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 3, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "4", result.getVisitSolution().get(2).getTask().getId());

    }

    /**
     * Tasks should be performed in opposite order when not starting at the office.
     * When starting at the office, there are two options: O -> 1 -> 2 -> D (Distance 7), O -> 2 -> 1 -> D (Distance 11).
     * Ending at last task,  -> 1 -> 2 -> D (Distance 6), -> 2 -> 1 -> D (Distance 5).
     * Therefore the order should reverse when using the end at last task or use open route option.
     */
    @Test
    public void swapOrderWhenNotReturningToOffice() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(0).getLocation(), 1);
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(1).getLocation(), 6);
        travelTimeMatrix.addDirectedConnection(allTasks.get(0).getLocation(), office, 4);
        travelTimeMatrix.addDirectedConnection(allTasks.get(1).getLocation(), office, 5);

        RouteEvaluator<ITask> routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());

        routeEvaluator.useOpenStartRoutes();
        result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "2", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Should have no travel time to the first task.", 0, result.getVisitSolution().get(0).getTravelTime());
    }

    @Test
    public void allTasksInsertFirstWithOpenStart() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 3));
        placeFarAwayFromRest(allTasks.get(3));
        routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(3), shift);

        Assert.assertEquals("Objective should be: ", 204, result.getObjectiveValue(), DELTA);
        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());

        routeEvaluator.useOpenStartRoutes();
        result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(3), shift);

        Assert.assertEquals("Objective should be: ", 104, result.getObjectiveValue(), DELTA);
        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "4", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "2", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "3", result.getVisitSolution().get(3).getTask().getId());
    }

    @Test
    public void noPhysicalAppearance() {
        TestTask noPhys1 = new TestTask(1, 0, 10, false, false, false, 0, 0, new TestLocation(false), "5");
        allTasks.add(0, noPhys1);
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        routeEvaluator = createRouteEvaluator(allTasks);
        routeEvaluator.useOpenStartRoutes();

        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(allTasks.subList(0, 1), allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "5", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Objective should be: ", 2.0, result.getObjectiveValue(), DELTA);
    }


    private void placeFarAwayFromRest(ITask task) {
        for (ITask otherTask : allTasks.stream().filter(i -> (i != task)).collect(Collectors.toSet())) {
            travelTimeMatrix.addUndirectedConnection(task.getLocation(), otherTask.getLocation(), 100);
        }
        travelTimeMatrix.addUndirectedConnection(task.getLocation(), office, 100);
    }


    private TestTravelTimeMatrix createTravelTimeMatrix(ILocation office, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix(tasks);
        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, taskA.getLocation(), 2);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 1);
        }
        return travelTimeMatrix;
    }

    private List<ITask> createTasks() {
        TestTask taskA = new TestTask(1, 0, 10, false, false, true, 0, 0, new TestLocation(false), "1");
        TestTask taskB = new TestTask(1, 10, 20, false, false, true, 0, 0, new TestLocation(false), "2");
        TestTask taskC = new TestTask(1, 20, 30, false, false, true, 0, 0, new TestLocation(false), "3");
        TestTask taskD = new TestTask(1, 30, 40, false, false, true, 0, 0, new TestLocation(false), "4");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }

    private RouteEvaluator<ITask> createRouteEvaluator(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        return routeEvaluator;
    }

    private RouteEvaluator<ITask> createRouteEvaluatorOverTime(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift("OverTime", 1, new OvertimeObjectiveFunction());
        return routeEvaluator;
    }

    private RouteEvaluator<ITask> createRouteEvaluatorTravelTime(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        return routeEvaluator;
    }
}