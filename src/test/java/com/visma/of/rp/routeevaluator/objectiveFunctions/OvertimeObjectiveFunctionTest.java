package com.visma.of.rp.routeevaluator.objectiveFunctions;


import com.visma.of.rp.routeevaluator.evaluation.objectives.OvertimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjectiveWithValues;
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

/**
 * Tests if the overtime constraint is implemented correctly.
 */
public class OvertimeObjectiveFunctionTest extends JUnitTestAbstract {

    List<ILocation> locations;
    List<ITask> allTasks;
    TestTravelTimeMatrix travelTimeMatrix;
    ILocation office;
    IShift shift;

    @Before
    public void init() {
        office = createOffice();
        locations = createLocations();
        allTasks = createTasks(locations);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        shift = new TestShift(0, 100);
    }

    @Test
    public void oneTaskNoOvertime() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(allTasks.get(0));
        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals("No overtime. ", 0, result.getObjectiveValue(), 1E-6);
        WeightedObjectiveWithValues objectiveStoreValues = evaluateRouteReturnIndividualObjectiveValues(tasks);
        Assert.assertEquals("Should have no overtime. ", 0, objectiveStoreValues.getObjectiveFunctionValue("OverTime"), 1E-6);

    }

    @Test
    public void oneTaskOvertime() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(createStandardTask(10, 90, 100));
        travelTimeMatrix.addUndirectedConnection(office, tasks.get(0).getLocation(), 5);

        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);
        Assert.assertEquals("Should have overtime. ", 5, result.getObjectiveValue(), 1E-6);

        WeightedObjectiveWithValues objectiveStoreValues = evaluateRouteReturnIndividualObjectiveValues(tasks);
        Assert.assertEquals("Should have overtime. ", 5, objectiveStoreValues.getObjectiveFunctionValue("OverTime"), 1E-6);

    }

    @Test
    public void fiveTasksFeasible() {
        RouteEvaluatorResult<ITask> result = evaluateRoute(allTasks);
        Assert.assertEquals("No overtime. ", 0, result.getObjectiveValue(), 1E-6);

        WeightedObjectiveWithValues objectiveStoreValues = evaluateRouteReturnIndividualObjectiveValues(allTasks);
        Assert.assertEquals("Should have overtime. ", 0, objectiveStoreValues.getObjectiveFunctionValue("OverTime"), 1E-6);

    }

    /**
     * Returns 1 minute late.
     */
    @Test
    public void sixTasksOvertime() {
        List<ITask> tasks = new ArrayList<>();
        ITask task6 = createStandardTask(10, 34, 100);
        locations.add(task6.getLocation());
        tasks.add(task6);
        tasks.addAll(allTasks);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals("Should have overtime. ", 1, result.getObjectiveValue(), 1E-6);
        WeightedObjectiveWithValues objectiveStoreValues = evaluateRouteReturnIndividualObjectiveValues(tasks);
        Assert.assertEquals("Should have overtime. ", 1, objectiveStoreValues.getObjectiveFunctionValue("OverTime"), 1E-6);

    }

    /**
     * Returns exactly on time.
     */
    @Test
    public void sixTasksNoOvertime() {
        List<ITask> tasks = new ArrayList<>();
        ITask task6 = createStandardTask(10, 33, 100);
        locations.add(task6.getLocation());
        tasks.add(task6);
        tasks.addAll(allTasks);
        travelTimeMatrix = createTravelTimeMatrix(locations, office);
        RouteEvaluatorResult<ITask> result = evaluateRoute(tasks);

        Assert.assertEquals("No overtime. ", 0, result.getObjectiveValue(), 1E-6);
        WeightedObjectiveWithValues objectiveStoreValues = evaluateRouteReturnIndividualObjectiveValues(tasks);
        Assert.assertEquals("Should have overtime. ", 0, objectiveStoreValues.getObjectiveFunctionValue("OverTime"), 1E-6);

    }


    private List<ITask> createTasks(List<ILocation> locations) {
        TestTask task1 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(0), "1");
        TestTask task2 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(1), "2");
        TestTask task3 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(2), "3");
        TestTask task4 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(3), "4");
        TestTask task5 = new TestTask(10, 0, 100, false, false, true, 0, 0, locations.get(4), "5");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        return tasks;
    }

    protected TestTravelTimeMatrix createTravelTimeMatrix(Collection<ILocation> locations, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ILocation locationA : locations) {
            travelTimeMatrix.addUndirectedConnection(office, locationA, 2);
            for (ILocation locationB : locations)
                if (locationA != locationB)
                    travelTimeMatrix.addUndirectedConnection(locationA, locationB, 1);
        }
        return travelTimeMatrix;
    }

    protected List<ILocation> createLocations() {
        List<ILocation> locations = new ArrayList<>();
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        locations.add(new TestLocation(false));
        return locations;
    }

    private RouteEvaluatorResult<ITask> evaluateRoute(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = getRouteEvaluator(tasks);
        return routeEvaluator.evaluateRouteByTheOrderOfTasks(tasks, new HashMap<>(), shift);
    }

    private WeightedObjectiveWithValues evaluateRouteReturnIndividualObjectiveValues(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = getRouteEvaluator(tasks);
        RouteEvaluatorResult<ITask> result = routeEvaluator.evaluateRouteByOrderOfTasksWithObjectiveValues(tasks, new HashMap<>(), shift);
        return (WeightedObjectiveWithValues) result.getObjective();
    }

    private RouteEvaluator<ITask> getRouteEvaluator(List<ITask> tasks) {
        RouteEvaluator<ITask> routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks, office);
        routeEvaluator.addObjectiveIntraShift("OverTime", 1, new OvertimeObjectiveFunction());
        return routeEvaluator;
    }
}
