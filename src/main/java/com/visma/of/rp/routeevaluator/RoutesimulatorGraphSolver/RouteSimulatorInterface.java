package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;

public interface RouteSimulatorInterface {
  RouteSimulatorResult simulateRoute(Individual individual, String employeeId);
}
