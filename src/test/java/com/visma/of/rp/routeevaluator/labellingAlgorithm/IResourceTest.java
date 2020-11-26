package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceOneElement;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests whether the IResource dominance rules are implemented correctly.
 */
public class IResourceTest {

    @Test
    public void resourceOneElementEqual() {
        ResourceOneElement resourceA = new ResourceOneElement(1);
        ResourceOneElement resourceB = new ResourceOneElement(1);
        Assert.assertEquals("Same number of elements, must be equal, i.e., zero.",0, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceOneElementFirstDominate() {
        ResourceOneElement resourceA = new ResourceOneElement(2);
        ResourceOneElement resourceB = new ResourceOneElement(1);
        Assert.assertEquals("First has higher number of elements, must dominate, i.e., minus one.",-1, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceOneElementSecondDominate() {
        ResourceOneElement resourceA = new ResourceOneElement(1);
        ResourceOneElement resourceB = new ResourceOneElement(2);
        Assert.assertEquals("Second has higher number of elements, must dominate, i.e., plus one.",1, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceOneElementSecondNullDominate() {
        ResourceOneElement resourceA = new ResourceOneElement(1);
        ResourceOneElement resourceB = null;
        Assert.assertEquals("Second is null, cannot determine must return two",2, resourceA.dominates(resourceB));
    }


    @Test
    public void resourceTwoElementsEqual() {
        ResourceTwoElements resourceA = new ResourceTwoElements(1,1);
        ResourceTwoElements resourceB = new ResourceTwoElements(1,1);
        Assert.assertEquals("Same number of elements, must be equal, i.e., zero.",0, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceTwoElementsFirstDominate() {
        ResourceTwoElements resourceA = new ResourceTwoElements(2,2);
        ResourceTwoElements resourceB = new ResourceTwoElements(1,2);
        Assert.assertEquals("First has higher number of elements, must dominate, i.e., minus one.",-1, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceTwoElementsSecondDominate() {
        ResourceTwoElements resourceA = new ResourceTwoElements(1,2);
        ResourceTwoElements resourceB = new ResourceTwoElements(2,3);
        Assert.assertEquals("Second has higher number of elements, must dominate, i.e., plus one.",1, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceTwoElementsNeitherDominates() {
        ResourceTwoElements resourceA = new ResourceTwoElements(3,1);
        ResourceTwoElements resourceB = new ResourceTwoElements(2,2);

        Assert.assertEquals("Second is better on element two, first is better on element one, " +
                "cannot dominate each other must return two",2, resourceA.dominates(resourceB));
    }

    @Test
    public void resourceTwoElementsSecondNullDominate() {
        ResourceTwoElements resourceA = new ResourceTwoElements(1,2);
        ResourceTwoElements resourceB = null;
        Assert.assertEquals("Second is null, cannot determine must return two",2, resourceA.dominates(resourceB));
    }
}
