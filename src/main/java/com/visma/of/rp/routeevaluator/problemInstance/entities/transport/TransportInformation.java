package com.visma.of.rp.routeevaluator.problemInstance.entities.transport;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import probleminstance.entities.address.AddressEntity;

import java.util.HashMap;
import java.util.Map;

public class TransportInformation {

    /**
     *
     * If parsing using JSON parser, remember to call the function ...
     *
     * setUpTravelTimesIncludingParkingBasedOnRawTravelTimes
     *
     * ... in order to set up the data structure transportModeToTravelTimeIncludingParking.
     *
     */

    @JsonProperty(value = ApiConstants.TRANSPORT_INFORMATION_TRANSPORTMODE_TO_BASIC_TRAVEL_TIME)
    private Map<Transport, Map<String, Map<String, Long>>> transportModeToRawTravelTime;
    @JsonIgnore
    private Map<Transport, Map<String, Map<String, Long>>> transportModeToTravelTimeIncludingParking;
    @JsonIgnore
    private long parkingTimeSeconds;

    @JsonCreator
    public TransportInformation(@JsonProperty(value = ApiConstants.TRANSPORT_INFORMATION_TRANSPORTMODE_TO_BASIC_TRAVEL_TIME, required = true) Map<Transport, Map<String, Map<String, Long>>> transportModeToRawTravelTime) {
        this.transportModeToRawTravelTime = transportModeToRawTravelTime;
    }

    public TransportInformation(long parkingTimeSeconds) {
        this.parkingTimeSeconds = parkingTimeSeconds;
        transportModeToTravelTimeIncludingParking = new HashMap<>();
        transportModeToRawTravelTime = new HashMap<>();
        addTransportModesInTravelTimes();
    }

    private void addTransportModesInTravelTimes() {
        for (Transport transport : Transport.values()) {
            transportModeToTravelTimeIncludingParking.putIfAbsent(transport, new HashMap<>());
            transportModeToRawTravelTime.putIfAbsent(transport, new HashMap<>());
        }
    }

    public void setUpTravelTimesIncludingParkingBasedOnRawTravelTimes(long parkingTimeSeconds) {
        this.parkingTimeSeconds = parkingTimeSeconds;
        transportModeToTravelTimeIncludingParking = new HashMap<>();
        addTransportModesInTravelTimes();
        for (Transport transport : transportModeToRawTravelTime.keySet()) {
            for (String from : transportModeToRawTravelTime.get(transport).keySet()) {
                for (String to : transportModeToRawTravelTime.get(transport).get(from).keySet()) {
                    setTravelTime(transport, from, to, transportModeToRawTravelTime.get(transport).get(from).get(to));
                }
            }
        }
    }

    private void setTravelTime(Transport transport, String from, String to, long rawTravelTime) {
        if (shouldAddParking(rawTravelTime, transport)) {
            rawTravelTime += parkingTimeSeconds;
        }
        transportModeToTravelTimeIncludingParking.get(transport).putIfAbsent(from, new HashMap<>());
        transportModeToTravelTimeIncludingParking.get(transport).get(from).putIfAbsent(to, rawTravelTime);
    }

    public void setRawTravelTime(Transport transport, String from, String to, long rawTravelTime) {
        transportModeToRawTravelTime.get(transport).putIfAbsent(from, new HashMap<>());
        transportModeToRawTravelTime.get(transport).get(from).putIfAbsent(to, rawTravelTime);
        setTravelTime(transport, from, to, rawTravelTime);
    }

    @JsonIgnore
    public boolean transportIsPossible(Transport transport, AddressEntity from, AddressEntity to) {
        if (!transportModeToTravelTimeIncludingParking.containsKey(transport))
            return false;
        if (!transportModeToTravelTimeIncludingParking.get(transport).containsKey(from.getId()))
            return false;
        if (!transportModeToTravelTimeIncludingParking.get(transport).get(from.getId()).containsKey(to.getId()))
            return false;
        return true;
    }


    public long getTravelTimeWithParkingFor(Transport transport, AddressEntity from, AddressEntity to) {
        return transportModeToTravelTimeIncludingParking.get(transport).get(from.getId()).get(to.getId());
    }

    public long getTravelTimeWithParkingFor(Transport transport, String fromId, String toId) {
        return transportModeToTravelTimeIncludingParking.get(transport).get(fromId).get(toId);
    }


    public Map<String, Long> getTravelTimeFromAddressEntityToAllOther(Transport transport, AddressEntity from) {
        return transportModeToTravelTimeIncludingParking.get(transport).get(from.getId());
    }


    public long getRawTravelTimeFor(Transport transport, AddressEntity from, AddressEntity to) {
        return transportModeToRawTravelTime.get(transport).get(from.getId()).get(to.getId());
    }


    @JsonIgnore
    public boolean isTravelTimeWithParkingEmpty() {
        return transportModeToTravelTimeIncludingParking.isEmpty();
    }

    @JsonIgnore
    public boolean isRawTravelTimesEmpty() {
        return transportModeToRawTravelTime.isEmpty();
    }

    private static boolean shouldAddParking(long secondsTravel, Transport transport) {
        return secondsTravel > 0 && transport.equals(Transport.DRIVE);
    }

}
