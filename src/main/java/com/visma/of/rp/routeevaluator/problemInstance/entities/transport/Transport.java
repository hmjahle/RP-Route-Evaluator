package com.visma.of.rp.routeevaluator.problemInstance.entities.transport;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Transport {

    @JsonProperty("Walk")
    WALK("Walk", "walking"),
    @JsonProperty("Drive")
    DRIVE("Drive", "driving"),
    @JsonProperty("Bicycle")
    BICYCLE("Bicycle", "bicycling");

    private String name;
    private String googleName;

    Transport(String name, String googleName){
        this.name = name;
        this.googleName = googleName;
    }

    public String getName() {
        return name;
    }

    public String getGoogleName() {
        return googleName;
    }

    @Override
    public String toString() {
        return name;
    }
}
