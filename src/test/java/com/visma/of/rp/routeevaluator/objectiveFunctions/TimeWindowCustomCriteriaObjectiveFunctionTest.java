package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.TimeWindowCustomCriteriaObjectiveFunction;
import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.IShift;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
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
import java.util.function.Function;

/**
 * Tests if the Time window objective return the correct objective values.
 */
public class TimeWindowCustomCriteriaObjectiveFunctionTest extends JUnitTestAbstract {

    ILocation office;
    TestTravelTimeMatrix travelTimeMatrix;
    IShift shift;
    List<ILocation> locations;
    List<ITask> allTasks;

    @Before
    public void init() {
        office = createOffice();
        locations = createLocations();
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(100, 0, 100);
        allTasks = createTasks();
    }

    @Test
    public void timeWindowCostOfOne() {
        TestTask task1 = new TestTask(1, 0, 10, true, true, true, 0, 0, locations.get(0), "1");
        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 10);

        RouteEvaluatorResult result = evaluateRoute(tasks, ITask::isStrict);
        Assert.assertEquals("Objective value must be.", 1, result.getObjectiveValue(), 1E-6);
        task1.setStrict(false);

        result = evaluateRoute(tasks, ITask::isStrict);
        Assert.assertEquals("Objective value must be.", 0, result.getObjectiveValue(), 1E-6);

        result = evaluateRoute(tasks, ITask::isSynced);
        Assert.assertEquals("Objective value must be.", 1, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void allNonPhysicalAppearanceCustomCriteria() {
        RouteEvaluatorResult result = evaluateRoute(allTasks, x -> !x.getRequirePhysicalAppearance());
        Assert.assertEquals("All has physical appearance.", 0, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void allPhysicalAppearanceCustomCriteria() {
        RouteEvaluatorResult result = evaluateRoute(allTasks, ITask::getRequirePhysicalAppearance);
        Assert.assertEquals("Four require physical appearance.", 4, result.getObjectiveValue(), 1E-6);

    }

    @Test
    public void syncedTaskCustomCriteria() {
        RouteEvaluatorResult result = evaluateRoute(allTasks, ITask::isSynced);
        Assert.assertEquals("Two is strict.", 2, result.getObjectiveValue(), 1E-6);
    }


    @Test
    public void strictTaskCustomCriteria() {
        RouteEvaluatorResult result = evaluateRoute(allTasks, ITask::isStrict);
        Assert.assertEquals("Three is strict.", 3, result.getObjectiveValue(), 1E-6);
    }


    @Test
    public void durationCustomCriteria() {
        RouteEvaluatorResult result = evaluateRoute(allTasks, x -> x.getDuration() > 1);
        Assert.assertEquals("Three has a duration of more than 1.", 3, result.getObjectiveValue(), 1E-6);
    }


    private List<ITask> createTasks() {
        TestTask task1 = new TestTask(1, 0, 10, true, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(5, 0, 20, true, true, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(5, 0, 30, true, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(5, 0, 40, false, true, true, 0, 0, locations.get(3), "4");
        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        return tasks;
    }

    private List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    private RouteEvaluatorResult evaluateRoute(List<ITask> tasks, Function<ITask, Boolean> criteriaFunction) {
        RouteEvaluator routeEvaluator = new RouteEvaluator(0, travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new TimeWindowCustomCriteriaObjectiveFunction(criteriaFunction));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, shift);
    }


    private TestTravelTimeMatrix createTravelTimeMatrix(Collection<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ILocation locationA : locations) {
            travelTimeMatrix.addUndirectedConnection(office, locationA, 10);
            for (ILocation locationB : locations)
                if (locationA != locationB)
                    travelTimeMatrix.addUndirectedConnection(locationA, locationB, 5);
        }
        return travelTimeMatrix;
    }
}
