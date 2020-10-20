package com.visma.of.rp.routeevaluator.simulateRouteFunctionalities;


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
 * Tests if the fitness found in the RouteSimulator is equal to the one calculated based on the RouteSimulator result.
 */
public class MergeTwoListsOfTasksTest extends JunitTest {

    private void initialize(String problemInstanceFolder) {
        super.initializeProblemInstance(problemInstanceFolder);
        super.initializeRouteSimulator();
    }

    @Test
    public void mergeTwoListsSameLength() {
        String problemInstanceFolder = "src/test/resources/testdata/mergeTwoListsOfTasksTest/";
        initialize(problemInstanceFolder);
        Individual testIndividual = new Individual(routeSimulator, problemInstance);

        List<Task> taskList1 = new ArrayList<>();
        taskList1.add(getTask("1"));
        taskList1.add(getTask("3"));
        taskList1.add(getTask("5"));

        List<Task> taskList2 = new ArrayList<>();
        taskList2.add(getTask("2"));
        taskList2.add(getTask("4"));
        taskList2.add(getTask("6"));

        String employeeId = "Sykepleier1";

        RouteSimulatorResult result = routeSimulator.simulateRouteMergingTaskLists(testIndividual, taskList1, taskList2, getEmployeeWorkShift(employeeId));
        List<Task> newTaskList = result.extractEmployeeRoute();
        List<Task> expectedList = getExpectedList();

        for (int i = 0; i < expectedList.size(); i++) {
            Assert.assertEquals("Tasks should be in optimal order.", expectedList.get(i), newTaskList.get(i));
        }
    }

    @Test
    public void mergeTwoListsDifferentLength() {
        String problemInstanceFolder = "src/test/resources/testdata/mergeTwoListsOfTasksTest/";
        initialize(problemInstanceFolder);
        Individual testIndividual = new Individual(routeSimulator, problemInstance);

        List<Task> taskList1 = new ArrayList<>();
        taskList1.add(getTask("1"));
        taskList1.add(getTask("2"));
        taskList1.add(getTask("3"));
        taskList1.add(getTask("5"));

        List<Task> taskList2 = new ArrayList<>();
        taskList2.add(getTask("4"));
        taskList2.add(getTask("6"));

        String employeeId = "Sykepleier1";

        mergeAndAssert(testIndividual, taskList1, taskList2, getEmployeeWorkShift(employeeId));
    }

    private void mergeAndAssert(Individual testIndividual, List<Task> taskList1, List<Task> taskList2, EmployeeWorkShift employeeWorkShift) {
        RouteSimulatorResult result = routeSimulator.simulateRouteMergingTaskLists(testIndividual, taskList1, taskList2, employeeWorkShift);
        List<Task> newTaskList = result.extractEmployeeRoute();
        List<Task> expectedList = getExpectedList();
        for (int i = 0; i < expectedList.size(); i++) {
            Assert.assertEquals("Tasks should be in optimal order.", expectedList.get(i), newTaskList.get(i));
        }
    }

    private List<Task> getExpectedList() {
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(getTask("1"));
        expectedList.add(getTask("2"));
        expectedList.add(getTask("3"));
        expectedList.add(getTask("4"));
        expectedList.add(getTask("5"));
        expectedList.add(getTask("6"));
        return expectedList;
    }

}