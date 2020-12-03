package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;

/**
 * Implements the ILocation interface and uses it to test methods in the route evaluator.
 */
public class TestLocation implements ILocation {

    String name;

    public TestLocation(boolean isOffice) {
        this.isOffice = isOffice;
    }

    public TestLocation(String name) {
        this.name = name;
    }
    boolean isOffice;

    int longitude;

    public TestLocation(boolean isOffice, int longitude, int latitude) {
        this.isOffice = isOffice;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }


    public int getLatitude() {
        return latitude;
    }


    int latitude;

    @Override
    public String toString()
    {
        return (" Name: " + name + "\tLong: " + longitude + "\tLat: " + latitude);
    }

}