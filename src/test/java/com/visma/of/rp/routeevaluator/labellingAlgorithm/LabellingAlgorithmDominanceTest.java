package com.visma.of.rp.routeevaluator.labellingAlgorithm;

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
import java.util.List;

public class LabellingAlgorithmDominanceTest extends JUnitTestAbstract {

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

    /**
     * Tasks should be performed in opposite order when not starting at the office.
     * When starting at the office, there are two options: O -> 1 -> 2 -> D (Distance 9), O -> 2 -> 1 -> D (Distance 8).
     * Task 2 can be dominated if resources are not handled correctly as it is not best before returning to the office.
     * A label at node 2 with objective exists that has only visited node two and a label at node 1 that has visited both
     * node 1 and two.
     */
    @Test
    public void dominanceBenefitWithTwoTasks() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(0).getLocation(), 1);
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(1).getLocation(), 4);
        travelTimeMatrix.addDirectedConnection(allTasks.get(0).getLocation(), office, 1);
        travelTimeMatrix.addDirectedConnection(allTasks.get(1).getLocation(), office, 5);

        RouteEvaluator routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 2, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "2", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "1", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Objective value should be.", 8, result.getObjectiveValue(), DELTA);
    }

    /**
     * Tasks should be performed in opposite order when not starting at the office.
     * When starting at the office, there are multiple options, but the optimal is O -> 3 -> 2 -> 1 -> D (Distance 9).
     * The algorithm should pay more at the start of the route in order to save later
     */
    @Test
    public void payExtraAtStartOfRouteToSaveLaterMultipleTasks() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(0));

        travelTimeMatrix.addDirectedConnection(office, allTasks.get(0).getLocation(), 2);
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(1).getLocation(), 5);
        travelTimeMatrix.addDirectedConnection(allTasks.get(0).getLocation(), office, 1);
        travelTimeMatrix.addDirectedConnection(allTasks.get(1).getLocation(), office, 4);

        travelTimeMatrix.addUndirectedConnection(office, allTasks.get(2).getLocation(), 4);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(2).getLocation(), allTasks.get(0).getLocation(), 3);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(1).getLocation(), allTasks.get(2).getLocation(), 1);

        RouteEvaluator routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 3, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "3", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "1", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Objective value should be.", 9, result.getObjectiveValue(), DELTA);

    }

    /**
     * Tasks should be performed in opposite order when not starting at the office.
     * When starting at the office, there are multiple options, but the optimal is O -> 3 -> 4 -> 5 -> 2 -> 1 -> D (Distance 12).
     * The algorithm should pay more at the start of the route in order to save later
     */
    @Test
    public void longRoute() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(3));
        tasks.add(allTasks.get(4));
        tasks.add(allTasks.get(0));

        List<ITask> tasksOpt = new ArrayList<>();
        tasksOpt.add(allTasks.get(2));
        tasksOpt.add(allTasks.get(3));
        tasksOpt.add(allTasks.get(4));
        tasksOpt.add(allTasks.get(1));
        tasksOpt.add(allTasks.get(0));

        travelTimeMatrix.addDirectedConnection(office, allTasks.get(0).getLocation(), 2);
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(1).getLocation(), 5);
        travelTimeMatrix.addDirectedConnection(allTasks.get(0).getLocation(), office, 1);
        travelTimeMatrix.addDirectedConnection(allTasks.get(1).getLocation(), office, 4);

        travelTimeMatrix.addUndirectedConnection(office, allTasks.get(2).getLocation(), 4);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(2).getLocation(), allTasks.get(0).getLocation(), 3);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(1).getLocation(), allTasks.get(2).getLocation(), 1);

        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), office, 5);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), allTasks.get(0).getLocation(), 4);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), allTasks.get(1).getLocation(), 2);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), allTasks.get(2).getLocation(), 1);

        travelTimeMatrix.addUndirectedConnection(allTasks.get(4).getLocation(), office, 5);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(4).getLocation(), allTasks.get(0).getLocation(), 4);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(4).getLocation(), allTasks.get(1).getLocation(), 2);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(4).getLocation(), allTasks.get(2).getLocation(), 1);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(4).getLocation(), allTasks.get(3).getLocation(), 1);

        RouteEvaluator routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTask(tasks, allTasks.get(1), shift);

        Assert.assertEquals("Number of visits should be: ", 5, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "3", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "4", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "5", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "2", result.getVisitSolution().get(3).getTask().getId());
        Assert.assertEquals("Fifth task id: ", "1", result.getVisitSolution().get(4).getTask().getId());
        Assert.assertEquals("Objective value should be.", 12, result.getObjectiveValue(), DELTA);

    }

    /**
     * Tasks should be performed in opposite order when not starting at the office.
     * When starting at the office, there are multiple options, but the optimal is O -> 3 -> 2 -> 4 -> 1 -> D (Distance 12).
     * The algorithm should pay more at the start of the route in order to save later
     */
    @Test
    public void mergeLists() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(2));
        tasks.add(allTasks.get(0));

        List<ITask> tasks2 = new ArrayList<>();
        tasks2.add(allTasks.get(1));
        tasks2.add(allTasks.get(3));

        travelTimeMatrix.addDirectedConnection(office, allTasks.get(0).getLocation(), 2);
        travelTimeMatrix.addDirectedConnection(office, allTasks.get(1).getLocation(), 5);
        travelTimeMatrix.addDirectedConnection(allTasks.get(0).getLocation(), office, 1);
        travelTimeMatrix.addDirectedConnection(allTasks.get(1).getLocation(), office, 4);

        travelTimeMatrix.addUndirectedConnection(office, allTasks.get(2).getLocation(), 4);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(2).getLocation(), allTasks.get(0).getLocation(), 3);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(1).getLocation(), allTasks.get(2).getLocation(), 1);

        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), office, 5);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), allTasks.get(0).getLocation(), 4);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), allTasks.get(1).getLocation(), 2);
        travelTimeMatrix.addUndirectedConnection(allTasks.get(3).getLocation(), allTasks.get(2).getLocation(), 1);

        RouteEvaluator routeEvaluator = createRouteEvaluatorTravelTime(allTasks);
        RouteEvaluatorResult result = routeEvaluator.evaluateRouteByTheOrderOfTasksInsertTasks(tasks, tasks2, shift);

        Assert.assertEquals("Number of visits should be: ", 4, result.getVisitSolution().size());
        Assert.assertEquals("First task id: ", "3", result.getVisitSolution().get(0).getTask().getId());
        Assert.assertEquals("Second task id: ", "2", result.getVisitSolution().get(1).getTask().getId());
        Assert.assertEquals("Third task id: ", "4", result.getVisitSolution().get(2).getTask().getId());
        Assert.assertEquals("Fourth task id: ", "1", result.getVisitSolution().get(3).getTask().getId());
        Assert.assertEquals("Objective value should be.", 12, result.getObjectiveValue(), DELTA);

    }


    private TestTravelTimeMatrix createTravelTimeMatrix(ILocation office, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, taskA.getLocation(), 100);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 3);
        }
        return travelTimeMatrix;
    }

    private List<ITask> createTasks() {
        TestTask taskA = new TestTask(1, 0, 100, false, false, true, 0, 0, new TestLocation(false), "1");
        TestTask taskB = new TestTask(1, 0, 100, false, false, true, 0, 0, new TestLocation(false), "2");
        TestTask taskC = new TestTask(1, 0, 100, false, false, true, 0, 0, new TestLocation(false), "3");
        TestTask taskD = new TestTask(1, 0, 100, false, false, true, 0, 0, new TestLocation(false), "4");
        TestTask taskE = new TestTask(1, 0, 100, false, false, true, 0, 0, new TestLocation(false), "5");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        tasks.add(taskE);
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