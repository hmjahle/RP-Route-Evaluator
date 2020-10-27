package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.ILocation;

/**
 * Implements the ILocation interface and uses it to test methods in the route evaluator.
 */
public class TestLocation implements ILocation {

    public TestLocation(boolean isOffice) {
        this.isOffice = isOffice;
    }

    boolean isOffice;

    @Override
    public boolean isOffice() {
        return false;
    }
}