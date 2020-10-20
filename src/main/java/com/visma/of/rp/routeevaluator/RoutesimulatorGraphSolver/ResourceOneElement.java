package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

/**
 * This class implement IResource, and has two elements. This should be used by INodeExtendSets with to elements.
 */
public class ResourceOneElement implements ResourceInterface {
    private int elementOneCount;

    public ResourceOneElement(int elementOneCount) {
        this.elementOneCount = elementOneCount;
    }

    @Override
    public ResourceOneElement extend(ExtendToInfo extendToInfo) {
        if (extendToInfo.getExtendNodeSetNumber() == 1)
            return new ResourceOneElement(elementOneCount + 1);
        else
            return new ResourceOneElement(elementOneCount);
    }

    public int getElementOneCount() {
        return elementOneCount;
    }

    @Override
    public int dominates(ResourceInterface resourceInterface) {
        ResourceOneElement other = (ResourceOneElement) resourceInterface;
        if (other == null)
            return 2;
        //Equals
        if (this.elementOneCount == other.elementOneCount)
            return 0;
        //This dominates other
        if (this.elementOneCount >= other.elementOneCount)
            return -1;

        //Other dominates
        return 1;
    }

    @Override
    public String toString() {
        return "ElementOne = " + elementOneCount;
    }
}
