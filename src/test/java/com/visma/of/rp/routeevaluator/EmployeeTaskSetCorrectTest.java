package com.visma.of.rp.routeevaluator;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.helpers.ResetAndInsertChromosome;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.EmployeeTask;
import routeplanner.solvers.fitness.entities.Visit;

public class EmployeeTaskSetCorrectTest extends JunitTest {


    @Before
    public void createTestIndividual() {
        ResetAndInsertChromosome.setConfiguration(configuration);
        String problemInstanceFolder = "src/test/resources/testdata/test_route_simulator/employee_task/";
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);

        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("1");
        ResetAndInsertChromosome.resetChromosomeForEmployee(employeeWorkShift, this.individual);
        String[] firstSet = {"emp1", "1", "5", "3", "4"};
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
    }

    @Test
    public void checksIfStartEndTimeAndDurationForEmployeeTaskIsCorrect() {
        Visit employeeTaskVisit = findVisit(getEmployeeWorkShift("1"), "emp1");
        Assert.assertTrue(employeeTaskVisit != null);
        testTimes("0920", employeeTaskVisit.getStart());
        testTimes("1000", employeeTaskVisit.getEnd());
        Assert.assertEquals(2400, employeeTaskVisit.getTask().getDurationSeconds());
    }

    private void testTimes(String expectedTime, long actualTime) {
        String actual = getDateHandler().getTimeStringFromSeconds(actualTime, "HHmm");
        Assert.assertEquals(expectedTime, actual);
    }

    private Visit findVisit(EmployeeWorkShift employeeWorkShift, String employeeTaskId) {
        for (Visit visit : individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution()) {
            if (visit.getTask() instanceof EmployeeTask && visit.getTask().getTaskId().equals(employeeTaskId)) {
                return visit;
            }
        }
        return null;
    }


}
