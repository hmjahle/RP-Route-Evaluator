package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.TravelTimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
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
        shift = new TestShift(100, 0, 100);
    }

    @Test
    public void noTasks() {
        List<ITask> tasks = new ArrayList<>();
        shift = new TestShift(100, 50, 150);

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, origin, destination);
        routeEvaluator.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("There should be no visits in the solution: ", 0, result.getVisitSolution().size());
        Assert.assertEquals("Must return at correct time!", 100, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 50.0, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void oneTask() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, origin, destination);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);

        Assert.assertEquals("Number of visits should be: ", 1, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(0).getStart());
        Assert.assertEquals("Office return should be: ", 41, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void multipleTasks() {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, allTasks, origin, destination);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasks(allTasks, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 10, result.getVisitSolution().get(0).getStart());
        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(1).getStart());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(2).getStart());
        Assert.assertEquals("Start time should be: ", 40, result.getVisitSolution().get(3).getStart());
        Assert.assertEquals("Office return should be: ", 51, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
    }


    private ITravelTimeMatrix createTravelTimeMatrix(ILocation origin, ILocation destination, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
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
}