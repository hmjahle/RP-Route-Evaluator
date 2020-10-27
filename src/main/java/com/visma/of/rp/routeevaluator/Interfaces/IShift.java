package com.visma.of.rp.routeevaluator.Interfaces;

import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;

public interface IShift {

    long getDurationSeconds();

    TransportMode getTransport();

    long getStartTime();

    long getEndTime();
}
