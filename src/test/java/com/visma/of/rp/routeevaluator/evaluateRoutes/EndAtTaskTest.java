package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.objectives.OvertimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TimeWindowObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.TravelTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.results.Visit;
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
import java.util.List;

public class EndAtTaskTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    RouteEvaluator routeEvaluator;
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

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), shift);
        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());

        Visit lastVisit = getLastVisit(result);
        Assert.assertEquals("Should end route at end time of last task.", result.getTimeOfArrivalAtDestination().intValue(),
                lastVisit.getStartTime() + lastVisit.getTask().getDuration());

    }

    private Visit getLastVisit(RouteEvaluatorResult result) {
        return result.getVisitSolution().get(result.getVisitSolution().size() - 1);
    }

    @Test
    public void oneTaskOvertime() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(createStandardTask(10, 90, 100));
        travelTimeMatrix.addUndirectedConnection(office, tasks.get(0).getLocation(), 5);
        RouteEvaluator routeEvaluator = createRouteEvaluatorOverTime(tasks);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertEquals("Should have overtime. ", 5, result.getObjectiveValue(), 1E-6);
        routeEvaluator.useOpenEndedRoutes();
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertEquals("Should have no overtime. ", 0, result.getObjectiveValue(), 1E-6);
        Visit lastVisit = getLastVisit(result);
        Assert.assertEquals("Should end route at end time of last task.", result.getTimeOfArrivalAtDestination().intValue(),
                lastVisit.getStartTime() + lastVisit.getTask().getDuration());
    }

    @Test
    public void allTasksInsertFirst() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(1, 4));
        routeEvaluator.useOpenEndedRoutes();

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(0), shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());

        Visit lastVisit = getLastVisit(result);
        Assert.assertEquals("Should end route at end time of last task.", result.getTimeOfArrivalAtDestination().intValue(),
                lastVisit.getStartTime() + lastVisit.getTask().getDuration());
    }

    @Test
    public void allTasksInsertMiddle() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        tasks.add(allTasks.get(3));
        routeEvaluator.useOpenEndedRoutes();

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 3, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "4", result.getVisitSolution().get(2).getTask().getId());

        Visit lastVisit = getLastVisit(result);
        Assert.assertEquals("Should end route at end time of last task.", result.getTimeOfArrivalAtDestination().intValue(),
                lastVisit.getStartTime() + lastVisit.getTask().getDuration());
    }


    /**
     * Tasks should be performed in opposite order when not returning to office.
     * Returning to office, there are two options: O -> 1 -> 2 -> D (Distance 6), O -> 2 -> 1 -> D (Distance 7).
     * Ending at last task,  O -> 1 -> 2 (Distance 5), O -> 2 -> 1 -> D (Distance 2).
     * Therefore the order should reverse when using the end at last task or use open route option
     */
    @Test
    public void swapOrderWhenNotReturningToOffice() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(0).getLocation(), 4);
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(1).getLocation(), 1);
        travelTimeMatrix.addDirectedConnection(allTasks.get(0).getLocation(), office, 5);
        travelTimeMatrix.addDirectedConnection(allTasks.get(1).getLocation(), office, 1);


        RouteEvaluator routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);
        print(result);
        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());

        routeEvaluator.useOpenEndedRoutes();
        result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);
        print(result);
        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "2", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());

        Visit lastVisit = getLastVisit(result);
        Assert.assertEquals("Should end route at end time of last task.", result.getTimeOfArrivalAtDestination().intValue(),
                lastVisit.getStartTime() + lastVisit.getTask().getDuration());
    }

    @Test
    public void allTasksInsertLast() {
        List<ITask> tasks = new ArrayList<>(allTasks.subList(0, 3));
        routeEvaluator.useOpenEndedRoutes();

        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(3), shift);
        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "1", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "3", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "4", result.getVisitSolution().get(3).getTask().getId());

        Visit lastVisit = getLastVisit(result);
        Assert.assertEquals("Should end route at end time of last task.", result.getTimeOfArrivalAtDestination().intValue(),
                lastVisit.getStartTime() + lastVisit.getTask().getDuration());
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

    private RouteEvaluator createRouteEvaluator(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        return routeEvaluator;
    }

    private RouteEvaluator createRouteEvaluatorOverTime(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift("OverTime", 1, new OvertimeObjectiveFunction());
        return routeEvaluator;
    }

    private RouteEvaluator createRouteEvaluatorTravelTime(List<ITask> tasks) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        return routeEvaluator;
    }
}