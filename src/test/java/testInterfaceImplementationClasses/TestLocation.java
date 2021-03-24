package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;

/**
 * Implements the ILocation interface and uses it to test methods in the route evaluator.
 */
public class TestLocation implements ILocation {

    String name;
    boolean isOffice;

    public TestLocation(boolean isOffice) {
        this.isOffice = isOffice;
        if(isOffice == true)
            name = "Office";
    }

    public TestLocation(String name) {
        this.name = name;
    }


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
    public String toString() {
        return (" Name: " + name + "\tLong: " + longitude + "\tLat: " + latitude);
    }

}