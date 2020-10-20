package com.visma.of.rp.routeevaluator;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.helpers.ResetAndInsertChromosome;
import probleminstance.entities.address.Patient;
import routeplanner.solvers.fitness.entities.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskSlackTest extends JunitTest {

    private String problemInstanceFolder = "src/test/resources/testdata/test_route_simulator/task_slack_test/";
    private ArrayList<Long> originalStartTimes = new ArrayList<>();
    private ArrayList<Long> originalEndTimes = new ArrayList<>();

    @Before
    public void before() {
        ResetAndInsertChromosome.setConfiguration(configuration);
        initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);
        resetChromosome();
        for (Visit visit : individual.getFitness().getEmployeeRouteSimulatorResultsFor(getEmployeeWorkShift("1")).getVisitSolution()) {
            if (visit.getTask().getAddressEntity() instanceof Patient) {
                originalStartTimes.add(visit.getTask().getStartTime());
            }
            originalEndTimes.add(visit.getTask().getEndTime());
        }
    }

    @Test
    public void test() {
        slackTest1();
        slackTest2();
        slackTest3();
        slackTest4();
    }

    private void slackTest1() {
        performTest(problemInstanceFolder + "slack_0/", 0);
    }

    private void slackTest2() {
        performTest(problemInstanceFolder + "slack_60/", 60);
    }

    private void slackTest3() {
        performTest(problemInstanceFolder + "slack_600/", 600);
    }

    private void slackTest4() {
        initializeIndividualAndProblemInstane(problemInstanceFolder + "slack_600000/");
        ArrayList<Long> expected = new ArrayList<>();
        long travelTimeOfficeToOne = 328;
        long travelOneToTwo = 83;
        long travelTwoToThree = 71;
        expected.add(originalEndTimes.get(0) + travelTimeOfficeToOne);
        expected.add(expected.get(0) + problemInstance.getTask("t1").getDurationSeconds() + travelOneToTwo);
        expected.add(expected.get(1) + problemInstance.getTask("t2").getDurationSeconds() + travelTwoToThree);
        compare(expected);
    }

    private void performTest(String problemInstanceFolder, long slackSeconds){
        initializeIndividualAndProblemInstane(problemInstanceFolder);
        compare(slackSeconds);
    }

    private void initializeIndividualAndProblemInstane(String problemInstanceFolder){
        initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);
        resetChromosome();
    }

    private void resetChromosome() {
        ResetAndInsertChromosome.resetChromosomeForEmployee(getEmployeeWorkShift("1"), individual);
        String[] firstSet = {"1-emp-1", "t1", "t2", "t3"};
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, getEmployeeWorkShift("1"), individual);
    }

    private void compare(long slack) {
        compare(originalStartTimes.stream().map(k -> k - slack).collect(Collectors.toList()));
    }

    private void compare(List<Long> expected) {
        int i = 0;
        for (Visit visit : individual.getFitness().getEmployeeRouteSimulatorResultsFor(getEmployeeWorkShift("1")).getVisitSolution()) {
            if (visit.getTask().getAddressEntity() instanceof Patient) {
                long startTime = visit.getStart();
                long expectedStartTime = expected.get(i);
                Assert.assertEquals(expectedStartTime, startTime);
                i++;
            }
        }
    }

}
