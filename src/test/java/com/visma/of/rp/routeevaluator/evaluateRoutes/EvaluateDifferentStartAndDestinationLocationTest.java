package com.visma.of.rp.routeevaluator.evaluateRoutes;

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
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EvaluateDifferentStartAndDestinationLocationTest extends JUnitTestAbstract {

    ILocation origin;
    ILocation destination;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    IShift shift;

    @Before
    public void initialize() {
        origin = createOffice();
        destination = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(origin, destination, allTasks);
        shift = new TestShift(0, 100);
    }

    @Test
    public void noTasks() {
        List<ITask> tasks = new ArrayList<>();
        shift = new TestShift(50, 150);

        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, origin, destination);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("There should be no visits in the solution: ", 0, result.getVisitSolution().size());
        Assert.assertEquals("Must return at correct time!", 100, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 50.0, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, origin, destination);
        assertOneTask(tasks, routeEvaluator);
    }

    @Test
    public void oneTaskNoStartDestination() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks);
        routeEvaluator.updateOrigin(origin);
        routeEvaluator.updateDestination(destination);
        assertOneTask(tasks, routeEvaluator);
    }

    @Test
    public void multipleTasks() {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, origin, destination);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        assertMultipleTasks(result);
    }

    @Test
    public void multipleTasksSetStartEndDestinationPostInitialization() {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, null, null);
        routeEvaluator.updateOrigin(origin);
        routeEvaluator.updateDestination(destination);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        assertMultipleTasks(result);
    }


    @Test
    public void multipleTasksNullStartEndDestination() {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, null, null);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        assertMultipleTasks(result);
    }

    @Test
    public void multipleTasksUpdateStart() {
        allTasks = createTasksNoTW();
        travelTimeMatrix = createTravelTimeMatrix(origin, destination, allTasks);

        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, origin, destination);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("Office return should be: ", 19, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateOrigin(destination);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When starting at the destination office return should be: ", 27, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateOrigin(allTasks.get(0).getLocation());
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When starting at the first node office return should be: ", 17, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateOrigin(allTasks.get(2).getLocation());
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When starting at another node office return should be: ", 18, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateOrigin(origin);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When starting at the destination office return should be: ", 19, result.getTimeOfArrivalAtDestination().longValue());
    }

    @Test
    public void multipleTasksUpdateDestination() {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, allTasks, origin, destination);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("Office return should be: ", 51, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateDestination(origin);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When returning to origin office return should be: ", 43, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateDestination(allTasks.get(1).getLocation());
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When returning to another node office return should be: ", 42, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateDestination(allTasks.get(3).getLocation());
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When returning to the previous node office return should be: ", 41, result.getTimeOfArrivalAtDestination().longValue());

        routeEvaluator.updateDestination(destination);
        result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);
        Assert.assertEquals("When returning to destination office return should be: ", 51, result.getTimeOfArrivalAtDestination().longValue());
    }

    private void assertOneTask(List<ITask> tasks, RouteEvaluator<ITask> routeEvaluator) {
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
        Assert.assertEquals("Number of visits should be: ", 1, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Office return should be: ", 41, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
    }

    private void assertMultipleTasks(RouteEvaluatorResult<ITask> result) {
        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 10, result.getVisitSolution().get(0).getStartTime());
        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(1).getStartTime());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(2).getStartTime());
        Assert.assertEquals("Start time should be: ", 40, result.getVisitSolution().get(3).getStartTime());
        Assert.assertEquals("Office return should be: ", 51, result.getTimeOfArrivalAtDestination().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
    }

    private ITravelTimeMatrix createTravelTimeMatrix(ILocation origin, ILocation destination, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix(tasks);
        travelTimeMatrix.addUndirectedConnection(origin, destination, 50);

        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(origin, taskA.getLocation(), 2);
            travelTimeMatrix.addUndirectedConnection(destination, taskA.getLocation(), 10);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 1);
        }
        return travelTimeMatrix;
    }

    private List<ITask> createTasks() {
        ITask taskA = createStandardTask(1, 10, 50);
        ITask taskB = createStandardTask(1, 20, 60);
        ITask taskC = createStandardTask(1, 30, 70);
        ITask taskD = createStandardTask(1, 40, 80);

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }

    private List<ITask> createTasksNoTW() {
        ITask taskA = createStandardTask(1, 0, 100);
        ITask taskB = createStandardTask(1, 0, 100);
        ITask taskC = createStandardTask(1, 0, 100);
        ITask taskD = createStandardTask(1, 0, 100);

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        return tasks;
    }
}
