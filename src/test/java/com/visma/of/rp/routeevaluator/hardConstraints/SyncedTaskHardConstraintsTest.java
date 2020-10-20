package com.visma.of.rp.routeevaluator.hardConstraints;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Test;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the functionality of synced tasks start at same time hard constraint.
 */
public class SyncedTaskHardConstraintsTest extends JunitTest {


    String problemInstanceFolder = "src/test/resources/testdata/hardConstraints/syncedTaskConstraint/";

    /**
     * Test if infeasible when constraint is active and synced on both employees does not start at same time.
     */
    @Test
    public void bothEmployeesBreakSynced() {
        initializeWithConstraint();
        setSyncedStartTimes();

        List<Task> tasks1 = getTasks1();
        List<Task> tasks2 = getTasks2();

        RouteSimulatorResult employee1ResultInfeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks1, getEmployeeWorkShift("1"));
        RouteSimulatorResult employee2ResultInfeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks2, getEmployeeWorkShift("2"));

        Assert.assertNull("Employee 1 should be infeasible", employee1ResultInfeasible);
        Assert.assertNull("Employee 2 should be infeasible", employee2ResultInfeasible);
    }

    /**
     * Test if feasible when constraint is not active and synced on both employees does not start at same time.
     */
    @Test
    public void bothEmployeesBreakSyncedWithoutConstraint() {
        initializeWithoutConstraint();
        setSyncedStartTimes();

        List<Task> tasks1 = getTasks1();
        List<Task> tasks2 = getTasks2();

        RouteSimulatorResult employee1ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks1, getEmployeeWorkShift("1"));
        RouteSimulatorResult employee2ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks2, getEmployeeWorkShift("2"));

        Assert.assertNotNull("Employee 1 should be feasible", employee1ResultFeasible);
        Assert.assertNotNull("Employee 2 should be feasible", employee2ResultFeasible);
    }

    /**
     * Test if one of three employees is infeasible when constraint is active.
     * When just one employee breaks constraint.
     */
    @Test
    public void oneOfThreeEmployeeBreakSynced() {
        initializeWithConstraint();
        setSyncedStartTimes();

        List<Task> tasks3 = getTasks3();
        List<Task> tasks4 = getTasks4();
        List<Task> tasks5 = getTasks5();

        RouteSimulatorResult employee1ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks3, getEmployeeWorkShift("1"));
        RouteSimulatorResult employee2ResultInfeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks4, getEmployeeWorkShift("2"));
        RouteSimulatorResult employee3ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks5, getEmployeeWorkShift("3"));

        Assert.assertNotNull("Employee 1 should be feasible", employee1ResultFeasible);
        Assert.assertNull("Employee 2 should be infeasible", employee2ResultInfeasible);
        Assert.assertNotNull("Employee 3 should be feasible", employee3ResultFeasible);
    }

    /**
     * Test if none employees is infeasible when constraint is in active.
     * When just one employee breaks constraint.
     */
    @Test
    public void oneOfThreeEmployeeBreakSyncedWithoutConstraint() {
        initializeWithoutConstraint();
        setSyncedStartTimes();

        List<Task> tasks3 = getTasks3();
        List<Task> tasks4 = getTasks4();
        List<Task> tasks5 = getTasks5();

        RouteSimulatorResult employee1ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks3, getEmployeeWorkShift("1"));
        RouteSimulatorResult employee2ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks4, getEmployeeWorkShift("2"));
        RouteSimulatorResult employee3ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks5, getEmployeeWorkShift("3"));

        Assert.assertNotNull("Employee 1 should be feasible", employee1ResultFeasible);
        Assert.assertNotNull("Employee 2 should be infeasible", employee2ResultFeasible);
        Assert.assertNotNull("Employee 3 should be feasible", employee3ResultFeasible);
    }


    /**
     * Test if feasible when constraint is active and synced on both employees start at the correct time.
     */
    @Test
    public void bothEmployeesDoesNotBreakSynced() {
        initializeWithConstraint();
        setSyncedStartTimes();

        List<Task> tasks6 = getTasks6();
        List<Task> tasks7 = getTasks7();

        RouteSimulatorResult employee1ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks6, getEmployeeWorkShift("1"));
        RouteSimulatorResult employee2ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks7, getEmployeeWorkShift("2"));


        Assert.assertNotNull("Employee 1 should be feasible", employee1ResultFeasible);
        Assert.assertNotNull("Employee 2 should be feasible", employee2ResultFeasible);
    }

    /**
     * Test if feasible when constraint is not active and synced on both employees start at the correct time.
     */
    @Test
    public void bothEmployeesDoesNotBreakSyncedWithoutConstraint() {
        initializeWithoutConstraint();
        setSyncedStartTimes();

        List<Task> tasks6 = getTasks6();
        List<Task> tasks7 = getTasks7();

        RouteSimulatorResult employee1ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks6, getEmployeeWorkShift("1"));
        RouteSimulatorResult employee2ResultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks7, getEmployeeWorkShift("2"));

        Assert.assertNotNull("Employee 1 should be feasible", employee1ResultFeasible);
        Assert.assertNotNull("Employee 2 should be feasible", employee2ResultFeasible);
    }


    public List<Task> getTasks1() {
        return Arrays.asList(getTask("2-sync-1"), getTask("1"), getTask("3-sync-1"));
    }

    public List<Task> getTasks2() {
        return Arrays.asList(getTask("3-sync-2"), getTask("2-sync-2"));
    }

    public List<Task> getTasks3() {
        return Arrays.asList(getTask("2-sync-1"), getTask("3-sync-1"));
    }

    public List<Task> getTasks4() {
        return Arrays.asList(getTask("6-sync-1"), getTask("3-sync-2"));
    }

    public List<Task> getTasks5() {
        return Arrays.asList(getTask("2-sync-2"), getTask("6-sync-2"));
    }

    public List<Task> getTasks6() {
        return Arrays.asList(getTask("2-sync-1"), getTask("3-sync-2"), getTask("1"), getTask("7"), getTask("6-sync-1"));
    }

    public List<Task> getTasks7() {
        return Arrays.asList(getTask("2-sync-2"), getTask("3-sync-1"), getTask("5"), getTask("6-sync-2"));
    }

    public void setSyncedStartTimes() {
        individual.getChromosome().setTaskStartTimeForSyncedTask(getTask("2-sync-1"), 1514793600);
        individual.getChromosome().setTaskStartTimeForSyncedTask(getTask("2-sync-2"), 1514793600);
        individual.getChromosome().setTaskStartTimeForSyncedTask(getTask("3-sync-1"), 1514797200);
        individual.getChromosome().setTaskStartTimeForSyncedTask(getTask("3-sync-2"), 1514797200);
        individual.getChromosome().setTaskStartTimeForSyncedTask(getTask("6-sync-1"), 1514808000);
        individual.getChromosome().setTaskStartTimeForSyncedTask(getTask("6-sync-2"), 1514808000);
    }


    private void initializeWithConstraint() {
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getOfficeInfo().setSyncedStartTimeIsHardConstraint(true);
        initializeRouteSimulator();
        individual = new Individual(routeSimulator, problemInstance);
    }

    private void initializeWithoutConstraint() {
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getOfficeInfo().setSyncedStartTimeIsHardConstraint(false);
        initializeRouteSimulator();
        individual = new Individual(routeSimulator, problemInstance);
    }
}
