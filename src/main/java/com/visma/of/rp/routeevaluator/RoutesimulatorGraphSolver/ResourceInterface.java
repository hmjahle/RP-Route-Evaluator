package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

/**
 * The ResourceInterface is used when extending labels. It must be able to dominated another resource of the same
 * implementation.
 */
public interface ResourceInterface {

    /**
     * The dominate function returns: -1 if this dominates other, 0 if equal, 1 if other dominates this and
     *  * 2 if they are not equal and neither dominates the other
     */
    int dominates(ResourceInterface other);

    /**
     * Returns a new resource based on the extend info, that holds information on which nodeSet it belongs to.
     */
    ResourceInterface extend(ExtendToInfo extendToInfo);
}
