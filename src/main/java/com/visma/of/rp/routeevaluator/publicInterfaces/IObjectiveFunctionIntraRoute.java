package com.visma.of.rp.routeevaluator.publicInterfaces;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.intraRouteEvaluationInfo.ObjectiveInfo;

/**
 * Interface for objectives that is solely evaluated within the route.
 */
public interface IObjectiveFunctionIntraRoute {

    /**
     * The function calculates the objective value of the objective for a part of the route.
     * Here a part of the route is referred to as performing a new task.
     *
     * @param objectiveInfo Holds information regarding the current state of the
     *                                 route used to calculate the objective value.
     * @return The change in objective value for this part of the route.
     */
    double calculateIncrementalObjectiveValueFor(ObjectiveInfo objectiveInfo);
}
