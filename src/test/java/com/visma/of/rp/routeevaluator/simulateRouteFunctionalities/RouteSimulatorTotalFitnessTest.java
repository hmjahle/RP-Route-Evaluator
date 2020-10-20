package com.visma.of.rp.routeevaluator.simulateRouteFunctionalities;


import junit.JunitTest;
import org.junit.Assert;
import org.junit.Test;
import other.helpers.ResetAndInsertChromosome;
import probleminstance.entities.Configuration;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.initial.InitializeIndividualMethods;
import routeplanner.solvers.tabu.neighborhoods.actions.Insert;

/**
 * Tests if the fitness found in the RouteSimulator is equal to
 * the one calculated based on the RouteSimulator result.
 * Hence all fitness values not related to intra route is not included.
 */
public class RouteSimulatorTotalFitnessTest extends JunitTest {



    public void SetConfiguration(Configuration config) {
        ResetAndInsertChromosome.setConfiguration(config);
        //Only compare intra fitness
        config.setWorkBalanceWeight(0);
        config.setVisitHistoryWeight(0);
        config.setOverqualifiedFitnessWeight(0);

        config.setEmployeeOvertimeFixedPenaltyForExceedingEndOfShift(10);
        config.setEmployeeOvertimePenaltyPerSecond(10);
        config.setEmployeeOvertimeSecondsBeforeEndOfShiftToStartPenalizing(10);

        problemInstance.getOfficeInfo().setRobustTimeMinutes(10000.0/60);
        problemInstance.getOfficeInfo().setParkingTimeSeconds(10);
        config.setStrictTasksStartTimeWeight(10);
        config.setTimeWindowForVisitWeightHigh(10);
        config.setTimeWindowForVisitWeightLow(10);
        config.setTravelTimeWeight(1);
        config.setEmployeeOvertimeWeight(10);
    }

    private void initialize(String problemInstanceFolder) {
        super.initializeProblemInstance(problemInstanceFolder);
        SetConfiguration(configuration);
        super.initializeRouteSimulator();
    }

    @Test
    public void extraTest() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test1/";
        initialize(problemInstanceFolder);
        initSpecific();
    }

    @Test
    public void ConfirmFitnessValue1() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test1/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue2() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test2/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue3() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test3/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue4() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test4/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue5() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test5/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue6() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test6/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue7() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test7/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    @Test
    public void ConfirmFitnessValue8() {
        String problemInstanceFolder = "src/test/resources/testdata/fitnessTests/test8/";
        initialize(problemInstanceFolder);
        PerformTest();
    }

    private void PerformTest() {
        for (int i = 0; i < 50; i++) {
            Individual expectedIndividual = new Individual(routeSimulator, problemInstance);
            InitializeIndividualMethods.initializeRandomly(expectedIndividual);
            Assert(expectedIndividual);
        }
    }

    private void Assert(Individual expectedIndividual) {
        for (EmployeeWorkShift employeeWorkShift : problemInstance.getEmployeeStructure().getActiveEmployeeWorkShifts()) {
            RouteSimulatorResult result = routeSimulator.simulateRoute(expectedIndividual, employeeWorkShift);
            expectedIndividual.getFitness().updateEmployeeFitness(result);
            double calculatedFitness = expectedIndividual.getFitness().getSingleEmployeeTotalFitness(employeeWorkShift);
            double routeSimulatorResultInternalFitness = result.getTotalFitness();
            Assert.assertEquals(routeSimulatorResultInternalFitness, calculatedFitness, 0.1);
        }
    }

    private void initSpecific() {
        Individual individual = new Individual(routeSimulator, problemInstance);
        new Insert(individual, "Sykepleier1", getTask("1+15594-sync-1"), 0).doAction();
        new Insert(individual, "Sykepleier2", getTask("1+15594-sync-2"), 0).doAction();
        Assert(individual);
    }
}
