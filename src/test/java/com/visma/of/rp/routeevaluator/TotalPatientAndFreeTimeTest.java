package com.visma.of.rp.routeevaluator;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.helpers.ResetAndInsertChromosome;
import probleminstance.entities.timeinterval.EmployeeWorkShift;


public class TotalPatientAndFreeTimeTest extends JunitTest {


    @Before
    public void createTestIndividual() {
        String problemInstanceFolder = "src/test/resources/testdata/test_route_simulator/total_patient_time/";
        ResetAndInsertChromosome.setConfiguration(configuration);
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("1");
        ResetAndInsertChromosome.resetChromosomeForEmployee(employeeWorkShift, this.individual);
        String[] firstSet = {"1", "2", "3", "4"};
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
    }


    @Test
    public void totalPatientTimeTest() {
        long totalVisitTime = individual.getFitness().getEmployeeRouteSimulatorResultsFor(getEmployeeWorkShift("1")).getTotalTimeToPatients();
        Assert.assertEquals(2400, totalVisitTime );
    }


    @Test
    public void testTotalFreeTime(){
        RouteSimulatorResult result = individual.getFitness().getEmployeeRouteSimulatorResultsFor(getEmployeeWorkShift("1"));
        long workDay = 28800;
        long patientTime = 2400;
        long totalTravelTime = result.getTotalTravelTimeIncludingParkingAndRobustness();
        long actualFreeTime = result.calculateAndGetTotalFreeTime();
        Assert.assertEquals(workDay - totalTravelTime - patientTime, actualFreeTime);
    }

}
