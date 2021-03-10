package com.visma.of.rp.routeevaluator.solver.algorithm;

/**
 * This class implement IResource, and has two elements. This should be used by INodeExtendSets with to elements.
 */
public class ResourceTwoElements implements IResource {

    private int elementOneCount;
    private int elementTwoCount;

    public ResourceTwoElements(int elementOneCount, int elementTwoCount) {
        this.elementOneCount = elementOneCount;
        this.elementTwoCount = elementTwoCount;
    }

    @Override
    public ResourceTwoElements extend(ExtendToInfo extendToInfo) {
        if (extendToInfo.getExtendNodeSetNumber() == 1)
            return new ResourceTwoElements(elementOneCount + 1, elementTwoCount);
        else if (extendToInfo.getExtendNodeSetNumber() == 2)
            return new ResourceTwoElements(elementOneCount, elementTwoCount + 1);
        else
            return new ResourceTwoElements(elementOneCount, elementTwoCount);
    }

    public int getElementOneCount() {
        return elementOneCount;
    }

    public int getElementTwoCount() {
        return elementTwoCount;
    }

    @Override
    public int dominates(IResource resourceInterface) {
        ResourceTwoElements other = (ResourceTwoElements) resourceInterface;
        //Not proper resource type
        if (other == null) {
            return 3;
        }
        //Equals
        if (this.elementOneCount == other.elementOneCount && this.elementTwoCount == other.elementTwoCount) {
            return 0;
        }
        //Other dominates
        else if (this.isEqualOrBetterOnBothElements(other)) {
            return 1;
        }
        //This dominates other
        if (other.isEqualOrBetterOnBothElements(this)) {
            return -1;
        }
        //Neither dominates
        return 2;
    }

    private boolean isEqualOrBetterOnBothElements(ResourceTwoElements other) {
        return other.elementOneCount >= this.elementOneCount && other.elementTwoCount >= this.elementTwoCount;
    }

    @Override
    public String toString() {
        return "ElementOneCount = " + elementOneCount + ", ElementTwoCount = " + elementTwoCount;
    }
}
