package testInterfaceImplementationClasses;

import com.visma.of.rp.routeevaluator.Interfaces.IPosition;

/**
 * Implements the IPosition interface and uses it to test methods in the route evaluator.
 */
public class TestPosition implements IPosition {

    public TestPosition(boolean isOffice) {
        this.isOffice = isOffice;
    }

    boolean isOffice;

    @Override
    public boolean isOffice() {
        return false;
    }
}