package com.visma.of.rp.routeevaluator.hardConstraints;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Test;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.finalizer.SolutionFeasibilityValidator;
import routeplanner.finalizer.StrictTasksFinalizer;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.hardConstraints.HardConstraintsIncremental;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.hardConstraints.StrictTimeWindowConstraint;
import routeplanner.solvers.tabu.neighborhoods.actions.Insert;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the functionality of over time hard constraint.
 */
public class StrictTimeWindowHardConstraintsTest extends JunitTest {


    String problemInstanceFolder = "src/test/resources/testdata/hardConstraints/strictTaskTimeWindowsConstraint/strictTime1/";
    EmployeeWorkShift employeeWorkShift;

    /**
     * Test if feasible when constraint is active and it does not break the time window .
     */
    @Test
    public void doesNotBreakTimeWindowConstraintActive() {
        initializeWithConstraint();
        List<Task> tasks = tasksDoesNotBreakTimeWindow();

        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNotNull("Should be feasible", resultFeasible);
    }

    /**
     * Test if infeasible when time window is broken and the constraint is active.
     */
    @Test
    public void breaksTimeWindowConstraintActive() {
        initializeWithConstraint();
        List<Task> tasks = tasksBreaksTimeWindow();

        RouteSimulatorResult resultInfeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNull("Route should be infeasible", resultInfeasible);
    }

    /**
     * Test if time window is broken when constraint is deactivated.
     */
    @Test
    public void timeWindowIsBrokenWhenConstraintNotActive() {
        initializeWithoutConstraint();
        List<Task> tasks = tasksBreaksTimeWindow();

        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNotNull("Route should be feasible", resultFeasible);
        Assert.assertTrue("Task 2 should break time window",
                resultFeasible.getVisitSolution().get(1).getEnd() > getTask("2").getEndTime());
    }

    /**
     * Test feasible when time window is not broken and constraint is deactivated.
     */
    @Test
    public void timeWindowIsNotBrokenWhenConstraintIsNotActive() {
        initializeWithoutConstraint();
        initializeRouteSimulator();
        List<Task> tasks = tasksDoesNotBreakTimeWindow();

        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        System.out.println(resultFeasible.getVisitSolution().get(0));

        Assert.assertNotNull("Route should be feasible", resultFeasible);
        Assert.assertFalse("Task 2 should not break time window",
                resultFeasible.getVisitSolution().get(0).getEnd() > getTask("2").getEndTime());
    }

    /**
     * Test if feasible when constraint is active and there is no strict tasks.
     */
    @Test
    public void doesNotBreakTimeWindowIfNotStrictConstraintActive() {
        initializeWithConstraint();
        List<Task> tasks = tasksNoStrictTimeWindows();

        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNotNull("Should be feasible", resultFeasible);
    }

    /**
     * Test if feasible when constraint is active and there is no strict tasks.
     */
    @Test
    public void doesNotBreakTimeWindowIfNotStrictConstraintNotActive() {
        initializeWithoutConstraint();
        List<Task> tasks = tasksNoStrictTimeWindows();

        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNotNull("Should be feasible", resultFeasible);
    }

    @Test
    public void feasibleIfStrictTaskIsWithinSlackThreshold() {
        String problemInstanceFolder = "src/test/resources/testdata/hardConstraints/strictTaskTimeWindowsConstraint/strictTime2/";
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getConfiguration().setStrictTaskFinalizerRemoveThresholdSeconds(60);
        initializeIndividual();
        new Insert(individual, getEmployeeWorkShift("1"), getTask("1"), 0).doAction();
        new Insert(individual, getEmployeeWorkShift("1"), getTask("2"), 1).doAction();

        StrictTasksFinalizer.removeTasksNotStartingAtCorrectTime(individual);
        Assert.assertTrue("Task 1 is still in the solution.", individual.taskIsAssigned(getTask("1")));
        Assert.assertTrue("Task 2 is still in the solution.", individual.taskIsAssigned(getTask("2")));

        setStrictTimeWindowToHardConstraint();

        RouteSimulatorResult routeSimulatorResult = routeSimulator.simulateRoute(individual, getEmployeeWorkShift("1"));
        Assert.assertNotNull("Route should be feasible.", routeSimulatorResult);
        Assert.assertTrue("Solution should be feasible", SolutionFeasibilityValidator.solutionIsFeasible(individual));
    }

    @Test
    public void feasibleIfStrictTaskIsWithinSlackThresholdZero() {
        String problemInstanceFolder = "src/test/resources/testdata/hardConstraints/strictTaskTimeWindowsConstraint/strictTime2/";
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getConfiguration().setStrictTaskFinalizerRemoveThresholdSeconds(0);
        initializeIndividual();
        new Insert(individual, getEmployeeWorkShift("1"), getTask("1"), 0).doAction();
        new Insert(individual, getEmployeeWorkShift("1"), getTask("2"), 1).doAction();

        setStrictTimeWindowToHardConstraint();
        RouteSimulatorResult routeSimulatorResult = routeSimulator.simulateRoute(individual, getEmployeeWorkShift("1"));
        Assert.assertNull("Route should be infeasible.", routeSimulatorResult);
        Assert.assertFalse("Solution should be infeasible", SolutionFeasibilityValidator.solutionIsFeasible(individual));

        StrictTasksFinalizer.removeTasksNotStartingAtCorrectTime(individual);
        Assert.assertTrue("Task 1 is still in the solution.", individual.taskIsAssigned(getTask("1")));
        Assert.assertFalse("Task 2 should be removed from the solution.", individual.taskIsAssigned(getTask("2")));
    }

    private void setStrictTimeWindowToHardConstraint() {
        HardConstraintsIncremental hardConstraintsIncremental = new HardConstraintsIncremental();
        hardConstraintsIncremental.addHardConstraint(new StrictTimeWindowConstraint(problemInstance));
        individual.getRouteSimulator().replaceHardConstraintsEvaluator(hardConstraintsIncremental);
    }


    private List<Task> tasksBreaksTimeWindow() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(getTask("1"));
        tasks.add(getTask("2"));
        tasks.add(getTask("3"));
        tasks.add(getTask("4"));
        tasks.add(getTask("5"));
        tasks.add(getTask("6"));
        tasks.add(getTask("7"));
        return tasks;
    }

    private List<Task> tasksDoesNotBreakTimeWindow() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(getTask("2"));
        tasks.add(getTask("1"));
        tasks.add(getTask("3"));
        tasks.add(getTask("4"));
        tasks.add(getTask("5"));
        tasks.add(getTask("6"));
        tasks.add(getTask("7"));
        return tasks;
    }

    private List<Task> tasksNoStrictTimeWindows() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(getTask("5"));
        tasks.add(getTask("6"));
        tasks.add(getTask("7"));
        return tasks;
    }

    private void initializeWithConstraint() {
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getOfficeInfo().setStrictTimeWindowsIsHardConstraint(true);
        initializeRouteSimulator();
        individual = new Individual(routeSimulator, problemInstance);
        employeeWorkShift = getEmployeeWorkShift("1");
    }

    private void initializeWithoutConstraint() {
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getOfficeInfo().setStrictTimeWindowsIsHardConstraint(false);
        initializeRouteSimulator();
        individual = new Individual(routeSimulator, problemInstance);
        employeeWorkShift = getEmployeeWorkShift("1");
    }
}
