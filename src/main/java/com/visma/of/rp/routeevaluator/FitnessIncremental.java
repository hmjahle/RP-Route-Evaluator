package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.problemInstance.ProblemInstance;
import probleminstance.ProblemInstance;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.MarginalFitnessInfo;
import routeplanner.solvers.fitness.employeefitness.EmployeeFitnessValue;

import java.util.ArrayList;
import java.util.List;

import static routeplanner.solvers.fitness.Fitness.addIntraEmployeeFitnessClasses;

public class FitnessIncremental {
    private List<EmployeeFitnessValue> employeeFitnessValues;

    public FitnessIncremental(ProblemInstance problemInstance) {
        employeeFitnessValues = new ArrayList<>();
        employeeFitnessValues.addAll(addIntraEmployeeFitnessClasses(problemInstance).values());
    }

    public double calculateIncrementalFitness(MarginalFitnessInfo fitnessInfo) {
        double fitness = 0.0;
        for (EmployeeFitnessValue employeeFitnessValue : employeeFitnessValues)
            fitness += employeeFitnessValue.getMarginalFitnessFor(fitnessInfo);
        return fitness;
    }


}
