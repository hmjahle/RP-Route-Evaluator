package com.visma.of.rp.routeevaluator.transportInfo;

public enum TransportModes {

    WALK("Walk", "walking"),

    DRIVE("Drive", "driving"),

    BICYCLE("Bicycle", "bicycling");

    private String name;
    private String googleName;

    TransportModes(String name, String googleName){
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
