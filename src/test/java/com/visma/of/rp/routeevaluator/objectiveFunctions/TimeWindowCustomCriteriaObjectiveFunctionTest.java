package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.evaluation.info.RouteEvaluationInfoAbstract;
import com.visma.of.rp.routeevaluator.evaluation.objectives.CustomCriteriaObjectiveFunction;
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
        shift = new TestShift(0, 100);
        allTasks = createTasks();
    }

    @Test
    public void timeWindowCostOfOne() {
        TestTask task1 = new TestTask(1, 0, 10, true, true, true, 0, 0, locations.get(0), "1");
        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        travelTimeMatrix.addUndirectedConnection(office, task1.getLocation(), 10);

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks, i -> !i.isDestination() && i.isStrict());
        Assert.assertEquals("ObjectiveAbstract value must be.", 1, result.getObjectiveValue(), 1E-6);
        task1.setStrict(false);

        result = evaluateRoute(tasks, i -> !i.isDestination() && i.isStrict());
        Assert.assertEquals("ObjectiveAbstract value must be.", 0, result.getObjectiveValue(), 1E-6);

        result = evaluateRoute(tasks, i -> !i.isDestination() && i.isSynced());
        Assert.assertEquals("ObjectiveAbstract value must be.", 1, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void allNonPhysicalAppearanceCustomCriteria() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks, i -> !i.isDestination() && !i.getTask().getRequirePhysicalAppearance());
        Assert.assertEquals("All has physical appearance.", 0, result.getObjectiveValue(), 1E-6);
    }

    @Test
    public void allPhysicalAppearanceCustomCriteria() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks, i -> !i.isDestination() && i.getTask().getRequirePhysicalAppearance());
        Assert.assertEquals("Four require physical appearance.", 4, result.getObjectiveValue(), 1E-6);

    }

    @Test
    public void syncedTaskCustomCriteria() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks, i -> !i.isDestination() && i.isSynced());
        Assert.assertEquals("Two is strict.", 2, result.getObjectiveValue(), 1E-6);
    }


    @Test
    public void strictTaskCustomCriteria() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks, i -> !i.isDestination() && i.isStrict());
        Assert.assertEquals("Three is strict.", 3, result.getObjectiveValue(), 1E-6);
    }


    @Test
    public void durationCustomCriteria() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks, x -> !x.isDestination() && x.getTask().getDuration() > 1);
        Assert.assertEquals("Three has a duration of more than 1.", 3, result.getObjectiveValue(), 1E-6);
    }


    protected List<ITask> createTasks() {
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

    protected List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    private RouteEvaluatorResult<ITask> evaluateRoute(List<ITask> tasks, Function<RouteEvaluationInfoAbstract, Boolean> criteriaFunction) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift(new CustomCriteriaObjectiveFunction(criteriaFunction, objectiveInfo -> Math.max(0.0, objectiveInfo.getVisitEnd() - objectiveInfo.getTask().getEndTime())));
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, getSyncedStartTimes(tasks), shift);
    }

}
