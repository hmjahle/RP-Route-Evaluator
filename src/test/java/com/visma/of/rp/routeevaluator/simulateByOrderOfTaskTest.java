package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.Interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.routeResult.RouteSimulatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import org.junit.Assert;
import org.junit.Test;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class simulateByOrderOfTaskTest extends JUnitTestAbstract {


    @Test
    public void simpleFiveTaskEvaluation() {

        ILocation office = createOffice();
        List<ITask> tasks = createTasks();
        ITravelTimeMatrix travelTimeMatrix = createTravelTimeMatrix(office, tasks);

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        IShift shift = new TestShift(100, 0, 100);

        RouteSimulatorResult result = routeEvaluator.simulateRouteByTheOrderOfTasks(tasks, null, shift);
        Assert.assertEquals("Number of visits should be: ", 5, result.getVisitSolution().size());
        Assert.assertEquals("Start time should be: ", 10, result.getVisitSolution().get(0).getStart());
        Assert.assertEquals("Start time should be: ", 20, result.getVisitSolution().get(1).getStart());
        Assert.assertEquals("Start time should be: ", 30, result.getVisitSolution().get(2).getStart());
        Assert.assertEquals("Start time should be: ", 40, result.getVisitSolution().get(3).getStart());
        Assert.assertEquals("Start time should be: ", 50, result.getVisitSolution().get(4).getStart());
        Assert.assertEquals("Office return should be: ", 53, result.getTimeOfOfficeReturn().longValue());
        Assert.assertEquals("Cost should be: ", 0.0, result.getObjectiveValue(), 1E-6);
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
        ITask taskA = createStandardTask(1, 10, 50);
        ITask taskB = createStandardTask(1, 20, 60);
        ITask taskC = createStandardTask(1, 30, 70);
        ITask taskD = createStandardTask(1, 40, 80);
        ITask taskE = createStandardTask(1, 50, 90);
        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        tasks.add(taskE);
        return tasks;
    }
}