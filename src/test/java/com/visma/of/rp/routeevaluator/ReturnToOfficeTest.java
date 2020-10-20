package com.visma.of.rp.routeevaluator;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Test;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;

import java.util.ArrayList;
import java.util.List;

public class ReturnToOfficeTest {

    private void initialize(String problemInstanceFolder) {
        super.initializeProblemInstance(problemInstanceFolder);
        super.initializeRouteSimulator();
    }

    @Test
    public void returnToOfficeTimeIsCorrect() {
        String problemInstanceFolder = "src/test/resources/testdata/route_simulator/office_return/";
        initialize(problemInstanceFolder);
        Individual testIndividual = new Individual(routeSimulator, problemInstance);
        List<Task> taskList = new ArrayList<>();
        taskList.add(getTask("1"));


        RouteSimulatorResult result = routeSimulator.simulateRouteByTheOrderOfTasks(testIndividual, taskList, getEmployeeWorkShift("Sykepleier1"));
        testIndividual.getFitness().updateEmployeeFitness(result);
        testIndividual.getFitness().updateSimulatedRouteFor(result);

        Assert.assertEquals("Must return at correct time!", 1516176124, result.getTimeOfOfficeReturn().longValue());
    }
}
