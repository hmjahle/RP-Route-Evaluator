package com.visma.of.rp.routeevaluator.Interfaces;

public interface IDistanceMatrix {
    boolean travelIsPossible(IPosition positionA, IPosition positionB);

    long getTravelTimeWithParking(IPosition positionA, IPosition positionB);

    long getTravelTime(IPosition positionA, IPosition positionB);
}
