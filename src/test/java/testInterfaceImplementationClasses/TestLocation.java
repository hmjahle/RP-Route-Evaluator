package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;

/**
 * Implements the ILocation interface and uses it to test methods in the route evaluator.
 */
public class TestLocation implements ILocation {

    public TestLocation(boolean isOffice) {
        this.isOffice = isOffice;
    }

    boolean isOffice;

    long longitude;

    public TestLocation(boolean isOffice, long longitude, long latitude) {
        this.isOffice = isOffice;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }


    public long getLatitude() {
        return latitude;
    }


    long latitude;

    @Override
    public String toString()
    {
        return ("Long: " + longitude + "\tLat: " + latitude);
    }

    @Override
    public boolean isOffice() {
        return false;
    }
}