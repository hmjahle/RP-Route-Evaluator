package com.visma.of.rp.routeevaluator.transportInfo;


public class TravelInfo {

    private long travelTimeWithParking;
    private long rawTravelTime;

    public TravelInfo(long travelTimeWithParking, long rawTravelTime) {
        // should not be driving if between same locations
        this.travelTimeWithParking = travelTimeWithParking;
        this.rawTravelTime = rawTravelTime;
    }


    public void addTravelInfo(long travelTimeWithParking, long travelTimeWithoutParking) {
        this.rawTravelTime += travelTimeWithoutParking;
        this.travelTimeWithParking += travelTimeWithParking;
    }

    public long getTravelTimeWithParking() {
        return travelTimeWithParking;
    }

    public long getRawTravelTime() {
        return rawTravelTime;
    }

}
