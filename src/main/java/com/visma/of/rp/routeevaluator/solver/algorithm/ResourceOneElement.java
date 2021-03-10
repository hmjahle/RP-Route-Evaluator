package com.visma.of.rp.routeevaluator.solver.algorithm;

/**
 * This class implement IResource, and has one element. This should be used by INodeExtendSets with to elements.
 */
public class ResourceOneElement implements IResource {

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

    /**
     * Implements the dominates function see the IResource.
     * @param resourceInterface Resource to check domination towards.
     * @return  See the interface documentation for the return values.
     */
    @Override
    public int dominates(IResource resourceInterface) {
        ResourceOneElement other = (ResourceOneElement) resourceInterface;
        if (other == null)
            return 3;
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
