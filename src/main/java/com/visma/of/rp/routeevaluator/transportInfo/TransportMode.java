package com.visma.of.rp.routeevaluator.transportInfo;

public enum TransportMode {

    WALK("Walk", "walking"),

    DRIVE("Drive", "driving"),

    BICYCLE("Bicycle", "bicycling");

    private String name;
    private String googleName;

    TransportMode(String name, String googleName){
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
