package com.visma.of.rp.routeevaluator.hardConstraints;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Test;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the functionality of over time hard constraint.
 */
public class OverTimeHardConstraintsTest extends JunitTest {


    String problemInstanceFolder = "src/test/resources/testdata/hardConstraints/overTimeConstraint/";


    /**
     * Test if feasible if it does not have overtime.
     */
    @Test
    public void hardConstraintsOvertimeTestNoOverTimeWithConstraintTest() {
        initializeWithConstraint();
        List<Task> tasks = tasksNoOvertime();
        String employeeId = "Sykepleier1";
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift(employeeId);
        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNotNull("Should be feasible", resultFeasible);
        Assert.assertEquals("Should have same overtime", resultFeasible.getTimeOfOfficeReturn(), resultFeasible.getTimeOfOfficeReturn());
    }

    /**
     * Test if infeasible when there is overtime.
     */
    @Test
    public void hardConstraintsOvertimeTestWithOverTimeAndConstraintTest() {
        initializeWithConstraint();
        List<Task> tasks = tasksOvertime();
        String employeeId = "Sykepleier1";
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift(employeeId);
        RouteSimulatorResult resultInfeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertNull("Route should be infeasible", resultInfeasible);
    }

    /**
     * Test feasible when there is overtime and the overtime constraint is deactivated.
     */
    @Test
    public void hardConstraintsOvertimeTestWithOverTimeNoConstraintTest() {
        initializeWithoutConstraint();
        List<Task> tasks = tasksOvertime();
        String employeeId = "Sykepleier1";
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift(employeeId);
        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertTrue("Route should  have overtime.", resultFeasible.getTimeOfOfficeReturn()
                > getEmployeeWorkShift(employeeId).getEndTime());
        Assert.assertNotNull("Route should be feasible", resultFeasible);
    }

    /**
     * Test feasible when there is no overtime and the overtime constraint is deactivated.
     */
    @Test
    public void hardConstraintsOvertimeTestNoOverTimeNoConstraintTest() {
        initializeWithoutConstraint();
        initializeRouteSimulator();
        List<Task> tasks = tasksNoOvertime();
        String employeeId = "Sykepleier1";
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift(employeeId);
        RouteSimulatorResult resultFeasible = routeSimulator.simulateRouteByTheOrderOfTasks(individual, tasks, employeeWorkShift);
        Assert.assertTrue("Route should not have overtime.", resultFeasible.getTimeOfOfficeReturn()
                < getEmployeeWorkShift(employeeId).getEndTime());
    }

    private List<Task> tasksOvertime() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(getTask("4"));
        tasks.add(getTask("6"));
        tasks.add(getTask("3"));
        tasks.add(getTask("1"));
        tasks.add(getTask("5"));
        tasks.add(getTask("2"));
        return tasks;
    }

    private List<Task> tasksNoOvertime() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(getTask("4"));
        tasks.add(getTask("6"));
        tasks.add(getTask("3"));
        tasks.add(getTask("1"));
        return tasks;
    }

    private void initializeWithConstraint() {
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getOfficeInfo().setOvertimeIsHardConstraint(true);
        initializeRouteSimulator();
        individual = new Individual(routeSimulator, problemInstance);
    }

    private void initializeWithoutConstraint() {
        initializeProblemInstance(problemInstanceFolder);
        problemInstance.getOfficeInfo().setOvertimeIsHardConstraint(false);
        initializeRouteSimulator();
        individual = new Individual(routeSimulator, problemInstance);
    }
}
