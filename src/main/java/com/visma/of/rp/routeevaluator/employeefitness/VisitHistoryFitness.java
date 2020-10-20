package com.visma.of.rp.routeevaluator.employeefitness;

import probleminstance.ProblemInstance;
import probleminstance.entities.address.Patient;
import probleminstance.initializers.visithistory.VisitHistoryFitnessCreator;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;
import routeplanner.solvers.fitness.entities.Visit;

public class VisitHistoryFitness extends EmployeeFitnessValue {


    public static final String ID = "VisitHistory";

    public VisitHistoryFitness(ProblemInstance problemInstance) {
        super(problemInstance, problemInstance.getConfiguration().getVisitHistoryWeight()
                * problemInstance.getOfficeInfo().getVisitHistoryProportionNormalized());
    }

    private VisitHistoryFitness(EmployeeFitnessValue copy) {
        super(copy);
    }

    private double getNewEmployeeValue(RouteSimulatorResult routeSimulatorResult) {
        VisitHistoryFitnessCreator fitnessCreator = problemInstance.getProblemInstanceHelpStructure().getVisitHistoryFitnessCreator();
        String originalEmployeeId = routeSimulatorResult.getEmployeeWorkShift().getOriginalEmployeeId();
        double newEmployeeValue = 0.0;
        for (Visit visit : routeSimulatorResult.getVisitSolution()) {
            if (visit.getTask().getAddressEntity() instanceof Patient) {
                String patientId = visit.getTask().getAddressEntity().getId();
                newEmployeeValue += fitnessCreator.getPenaltyFor(originalEmployeeId, patientId);
            }
        }
        return newEmployeeValue;
    }

    @Override
    public double updateFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newEmployeeValue = getNewEmployeeValue(routeSimulatorResult);
        return super.setValue(routeSimulatorResult.getEmployeeWorkShift(), newEmployeeValue);
    }

    @Override
    public double getDeltaFitnessValueForEmployee(RouteSimulatorResult routeSimulatorResult, Individual individual) {
        double newEmployeeValue = getNewEmployeeValue(routeSimulatorResult);
        return super.getDeltaFitness(routeSimulatorResult.getEmployeeWorkShift(), newEmployeeValue);
    }

    @Override
    public double getMarginalFitnessFor(MarginalFitnessInfo marginalFitnessInfo) {
        return 0;
    }

    @Override
    public EmployeeFitnessValue cloneEmployeeFitnessValue() {
        return new VisitHistoryFitness(this);
    }

}
