package com.visma.of.rp.routeevaluator.Interfaces;

import com.visma.of.rp.routeevaluator.transportInfo.TransportModes;

public interface IShift {

    long getDurationSeconds();

    TransportModes getTransport();

    long getStartTime();

    long getEndTime();
}
