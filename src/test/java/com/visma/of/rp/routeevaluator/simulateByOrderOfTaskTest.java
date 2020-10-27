package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.Interfaces.IDistanceMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.IPosition;
import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteSimulatorResult;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;
import org.junit.Assert;
import org.junit.Test;
import testSupport.JUnitTestAbstract;
import testInterfaceImplementationClasses.*;

import java.util.*;

public class simulateByOrderOfTaskTest extends JUnitTestAbstract {


    @Test
    public void simpleFiveTaskEvaluation() {

        IPosition office = createOffice();
        List<ITask> tasks = createTasks();
        Map<TransportMode, IDistanceMatrix> distanceMatrices = createIDistanceMatrix(office, tasks);

        RouteEvaluator routeEvaluator = new RouteEvaluator(0, distanceMatrices, tasks, office);
        IShift shift = new TestShift(100, 0, 100, TransportMode.DRIVE);

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

    private Map<TransportMode, IDistanceMatrix> createIDistanceMatrix(IPosition office, Collection<ITask> tasks) {
        TestDistanceMatrix distanceMatrix = new TestDistanceMatrix();
        for (ITask taskA : tasks) {
            distanceMatrix.addUndirectedConnection(office, taskA.getPosition(), 2);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    distanceMatrix.addUndirectedConnection(taskA.getPosition(), taskB.getPosition(), 1);
        }
        Map<TransportMode, IDistanceMatrix> distanceMatrices = new HashMap<>();
        distanceMatrices.put(TransportMode.DRIVE, distanceMatrix);
        return distanceMatrices;
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