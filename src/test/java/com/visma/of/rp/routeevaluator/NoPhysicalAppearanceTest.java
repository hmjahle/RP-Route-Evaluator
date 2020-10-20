package com.visma.of.rp.routeevaluator;

import junit.JunitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.helpers.ResetAndInsertChromosome;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.transport.Transport;

public class NoPhysicalAppearanceTest extends JunitTest {

    private String problemInstanceFolder;
    private String problemInstanceFolder2;

    @Before
    public void createProblemInstance() {
        problemInstanceFolder = "src/test/resources/testdata/test_route_simulator/no_physical_apparance/";
        problemInstanceFolder2 = "src/test/resources/testdata/test_route_simulator/no_physical_apparance2/";
        ResetAndInsertChromosome.setConfiguration(configuration);
    }

    @Test
    public void testVisitListWithoutRobustness() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);
        String[] firstSet = {"1", "2", "3", "4", "5", "6"};
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("080528", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("081656", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));
        Assert.assertEquals("083000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(2).getStart(), "HHmmss"));
        Assert.assertEquals("084000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(3).getStart(), "HHmmss"));
        Assert.assertEquals("085220", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(4).getStart(), "HHmmss"));
        Assert.assertEquals("090503", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(5).getStart(), "HHmmss"));
    }

    @Test
    public void testVisitListWithRobustness() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder + "/robust_1/");
        String[] firstSet = {"1", "2", "3", "4", "5", "6"};
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("080528", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("081657", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));
        Assert.assertEquals("083000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(2).getStart(), "HHmmss"));
        Assert.assertEquals("084001", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(3).getStart(), "HHmmss"));
        Assert.assertEquals("085222", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(4).getStart(), "HHmmss"));
        Assert.assertEquals("090506", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(5).getStart(), "HHmmss"));
    }

    @Test
    public void testVisitSingleNonPhysical() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);
        String[] firstSet = {"3"};
        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("083000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals(0.0, individual.getFitness().getTotalFitness(), 0.001);
        double totalTravelTime = 0.0;
        for (EmployeeWorkShift employeeWorkShift1 : individual.getChromosome().getEmployeeWorkShiftsInChromosome())
            totalTravelTime += routeSimulator.simulateRoute(individual, employeeWorkShift1).getTotalTravelTime();
        Assert.assertEquals(0.0, totalTravelTime, 0.0001);
    }

    @Test
    public void testVisitOneNonPhysicalOnePhysical1() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder2);
        //Non physical, physical
        String[] firstSet = {"3", "7"};

        double expectedTravelTime = problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("7").getAddressEntity(), problemInstance.getOfficeInfo());
        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getOfficeInfo(), problemInstance.getTask("7").getAddressEntity());

        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("093000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("130000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));


        double totalTravelTime = 0.0;
        for (EmployeeWorkShift employeeWorkShift1 : individual.getChromosome().getEmployeeWorkShiftsInChromosome())
            totalTravelTime += routeSimulator.simulateRoute(individual, employeeWorkShift1).getTotalTravelTime();
        Assert.assertEquals(expectedTravelTime, totalTravelTime, 0.0001);
    }

    @Test
    public void testVisitOneNonPhysicalOnePhysical2() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder2);
        //Physical, non physical
        String[] firstSet = {"1", "3"};

        double expectedTravelTime = problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("1").getAddressEntity(), problemInstance.getOfficeInfo());
        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getOfficeInfo(), problemInstance.getTask("1").getAddressEntity());

        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("081500", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("093000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));


        double totalTravelTime = 0.0;
        for (EmployeeWorkShift employeeWorkShift1 : individual.getChromosome().getEmployeeWorkShiftsInChromosome())
            totalTravelTime += routeSimulator.simulateRoute(individual, employeeWorkShift1).getTotalTravelTime();
        Assert.assertEquals(expectedTravelTime, totalTravelTime, 0.0001);
    }

    @Test
    public void testVisitMultiPleNonPhysicalOnePhysical() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder2);
        String[] firstSet = {"3", "7", "8", "9"};

        double expectedTravelTime = problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("7").getAddressEntity(), problemInstance.getOfficeInfo());
        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getOfficeInfo(), problemInstance.getTask("7").getAddressEntity());

        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("093000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("130000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));
        Assert.assertEquals("140000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(2).getStart(), "HHmmss"));
        Assert.assertEquals("150000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(3).getStart(), "HHmmss"));


        double totalTravelTime = 0.0;
        for (EmployeeWorkShift employeeWorkShift1 : individual.getChromosome().getEmployeeWorkShiftsInChromosome())
            totalTravelTime += routeSimulator.simulateRoute(individual, employeeWorkShift1).getTotalTravelTime();
        Assert.assertEquals(expectedTravelTime, totalTravelTime, 0.0001);
    }

    @Test
    public void testVisitMultiPleNonPhysicalTwoPhysical() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder2);
        String[] firstSet = {"3", "5", "7", "8", "9"};

        double expectedTravelTime = problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getOfficeInfo(), problemInstance.getTask("5").getAddressEntity());
        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("5").getAddressEntity(), problemInstance.getTask("7").getAddressEntity());
        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("7").getAddressEntity(), problemInstance.getOfficeInfo());

        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("093000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("110000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));
        Assert.assertEquals("130000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(2).getStart(), "HHmmss"));
        Assert.assertEquals("140000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(3).getStart(), "HHmmss"));
        Assert.assertEquals("150000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(4).getStart(), "HHmmss"));


        double totalTravelTime = 0.0;
        for (EmployeeWorkShift employeeWorkShift1 : individual.getChromosome().getEmployeeWorkShiftsInChromosome())
            totalTravelTime += routeSimulator.simulateRoute(individual, employeeWorkShift1).getTotalTravelTime();
        Assert.assertEquals(expectedTravelTime, totalTravelTime, 0.0001);
    }


    @Test
    public void testVisitMixed() {
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder2);
        String[] firstSet = {"1", "2", "3", "4", "8"};

        double expectedTravelTime = problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getOfficeInfo(), problemInstance.getTask("1").getAddressEntity());

        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("1").getAddressEntity(), problemInstance.getTask("2").getAddressEntity());

        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("2").getAddressEntity(), problemInstance.getTask("4").getAddressEntity());

        expectedTravelTime += problemInstance.getProblemInstanceHelpStructure().getTransportInformation().getTravelTimeWithParkingFor(
                Transport.DRIVE, problemInstance.getTask("4").getAddressEntity(), problemInstance.getOfficeInfo());

        EmployeeWorkShift employeeWorkShift = getEmployeeWorkShift("emp1");
        ResetAndInsertChromosome.insertTasksOnEmployee(firstSet, employeeWorkShift, individual);
        Assert.assertEquals("081500", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(0).getStart(), "HHmmss"));
        Assert.assertEquals("083000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(1).getStart(), "HHmmss"));
        Assert.assertEquals("093000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(2).getStart(), "HHmmss"));
        Assert.assertEquals("100000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(3).getStart(), "HHmmss"));
        Assert.assertEquals("140000", getDateHandler().getTimeStringFromSeconds(individual.getFitness().getEmployeeRouteSimulatorResultsFor(employeeWorkShift).getVisitSolution().get(4).getStart(), "HHmmss"));


        double totalTravelTime = 0.0;
        for (EmployeeWorkShift employeeWorkShift1 : individual.getChromosome().getEmployeeWorkShiftsInChromosome())
            totalTravelTime += routeSimulator.simulateRoute(individual, employeeWorkShift1).getTotalTravelTime();
        Assert.assertEquals(expectedTravelTime, totalTravelTime, 0.0001);
    }
}
