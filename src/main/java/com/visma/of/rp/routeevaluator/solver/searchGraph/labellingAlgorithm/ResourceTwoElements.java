package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;

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
        if (other == null)
            return 2;
        //Equals
        if (this.elementTwoCount == other.elementTwoCount && this.elementOneCount == other.elementOneCount)
            return 0;
        //This dominates other
        if (this.elementTwoCount >= other.elementTwoCount && this.elementOneCount >= other.elementOneCount)
            return -1;
            //Other dominates
        else if (this.elementTwoCount <= other.elementTwoCount && this.elementOneCount <= other.elementOneCount)
            return 1;
        //Neither dominates
        return 2;
    }

    @Override
    public String toString() {
        return "ElementOneCount = " + elementOneCount + ", ElementTwoCount = " + elementTwoCount;
    }
}
