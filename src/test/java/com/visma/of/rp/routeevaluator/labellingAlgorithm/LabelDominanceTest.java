package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests whether the labelling dominance rules are implemented correctly.
 */
public class LabelDominanceTest {


    @Test
    public void resourceAndObjectiveValue() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);
        Label labelA = new Label(null, null, 0, costA, resourcesA, 3, 0);
        Label labelB = new Label(null, null, 0, costB, resourcesA, 3, 0);
        Label labelC = new Label(null, null, 0, costA, resourcesB, 3, 0);
        Label labelD = new Label(null, null, 0, costB, resourcesB, 3, 0);
        labelA.setCanLeaveLocationAtTime(0);
        labelB.setCanLeaveLocationAtTime(0);
        labelC.setCanLeaveLocationAtTime(0);
        labelD.setCanLeaveLocationAtTime(0);


        assertEquals(labelA, labelB, labelC, labelD);

        Assert.assertEquals("5", -1, labelTest(labelA, labelB));
        Assert.assertEquals("6", 1, labelTest(labelB, labelA));

        Assert.assertEquals("7", -1, labelTest(labelA, labelC));
        Assert.assertEquals("8", 1, labelTest(labelC, labelA));

        Assert.assertEquals("9", -1, labelTest(labelA, labelD));
        Assert.assertEquals("10", 1, labelTest(labelD, labelA));

        Assert.assertEquals("11", -1, labelTest(labelC, labelD));
        Assert.assertEquals("12", 1, labelTest(labelD, labelC));

        Assert.assertEquals("13", 2, labelTest(labelB, labelC));
        Assert.assertEquals("14", 2, labelTest(labelC, labelB));


    }

    @Test
    public void currentTimeAndDrivingTime() {
        WeightedObjective costA = new WeightedObjective(0);
        ResourceTwoElements resources = new ResourceTwoElements(0, 0);

        Label labelA = new Label(null, null, 0, costA, resources, 1, 0);
        Label labelB = new Label(null, null, 0, costA, resources, 2, 0);
        Label labelC = new Label(null, null, 0, costA, resources, 3, 0);
        Label labelD = new Label(null, null, 0, costA, resources, 4, 0);
        labelA.setCanLeaveLocationAtTime(2);
        labelB.setCanLeaveLocationAtTime(0);
        labelC.setCanLeaveLocationAtTime(3);
        labelD.setCanLeaveLocationAtTime(4);

        Assert.assertEquals("1", 2, labelTest(labelA, labelB));
        Assert.assertEquals("2", 2, labelTest(labelB, labelA));

        assertNotEqualA(labelA, labelB, labelC, labelD);

        Assert.assertEquals("13", 0, labelTest(labelA, labelA));
        Assert.assertEquals("14", 0, labelTest(labelB, labelB));

        Assert.assertEquals("15", 0, labelTest(labelC, labelC));
        Assert.assertEquals("16", 0, labelTest(labelD, labelD));

    }


    @Test
    public void currentTimeAndDrivingTimeAndObjectiveValue() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);
        ResourceTwoElements resources = new ResourceTwoElements(0, 0);

        Label labelA = new Label(null, null, 0, costA, resources, 1, 0);
        Label labelB = new Label(null, null, 0, costA, resources, 2, 0);
        Label labelC = new Label(null, null, 0, costB, resources, 1, 0);
        Label labelD = new Label(null, null, 0, costB, resources, 2, 0);
        labelA.setCanLeaveLocationAtTime(1);
        labelB.setCanLeaveLocationAtTime(2);
        labelC.setCanLeaveLocationAtTime(1);
        labelD.setCanLeaveLocationAtTime(2);

        Assert.assertEquals("1", -1, labelTest(labelA, labelC));
        Assert.assertEquals("2", 1, labelTest(labelC, labelA));

        Assert.assertEquals("3", -1, labelTest(labelA, labelD));
        Assert.assertEquals("4", 1, labelTest(labelD, labelA));


        Assert.assertEquals("5", 2, labelTest(labelB, labelC));
        Assert.assertEquals("6", 2, labelTest(labelC, labelB));

        Assert.assertEquals("7", -1, labelTest(labelB, labelD));
        Assert.assertEquals("8", 1, labelTest(labelD, labelB));

        Assert.assertEquals("9", 0, labelTest(labelA, labelA));
        Assert.assertEquals("10", 0, labelTest(labelB, labelB));

        Assert.assertEquals("11", 0, labelTest(labelC, labelC));
        Assert.assertEquals("12", 0, labelTest(labelD, labelD));
    }

    @Test
    public void currentTimeAndDrivingTimeAndObjectiveValueAndResources() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(0, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 0);

        Label labelA = new Label(null, null, 0, costA, resourcesA, 1, 0);
        Label labelB = new Label(null, null, 0, costA, resourcesA, 2, 0);

        Label labelC = new Label(null, null, 0, costB, resourcesB, 1, 0);
        Label labelD = new Label(null, null, 0, costB, resourcesB, 2, 0);
        labelA.setCanLeaveLocationAtTime(1);
        labelB.setCanLeaveLocationAtTime(2);
        labelC.setCanLeaveLocationAtTime(1);
        labelD.setCanLeaveLocationAtTime(2);

        Assert.assertEquals("1", -1, labelTest(labelA, labelB));
        Assert.assertEquals("2", 1, labelTest(labelB, labelA));

        assertSecondAlwaysDominatesFirst(labelA, labelB, labelC, labelD);

        Assert.assertEquals("11", -1, labelTest(labelC, labelD));
        Assert.assertEquals("12", 1, labelTest(labelD, labelC));

        Assert.assertEquals("13", 0, labelTest(labelA, labelA));
        Assert.assertEquals("14", 0, labelTest(labelB, labelB));

        Assert.assertEquals("15", 0, labelTest(labelC, labelC));
        Assert.assertEquals("16", 0, labelTest(labelD, labelD));
    }


    @Test
    public void objectiveValueAndResources() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(1, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        Label labelA = new Label(null, null, 0, costA, resourcesA, 1, 0);
        Label labelB = new Label(null, null, 0, costB, resourcesA, 1, 0);

        Label labelC = new Label(null, null, 0, costA, resourcesB, 1, 0);
        Label labelD = new Label(null, null, 0, costB, resourcesB, 1, 0);
        labelA.setCanLeaveLocationAtTime(0);
        labelB.setCanLeaveLocationAtTime(0);
        labelC.setCanLeaveLocationAtTime(0);
        labelD.setCanLeaveLocationAtTime(0);

        assertNotEqualB(labelA, labelB, labelC, labelD);

        Assert.assertEquals("5", 2, labelTest(labelA, labelD));
        Assert.assertEquals("6", 2, labelTest(labelD, labelA));

        Assert.assertEquals("13", 0, labelTest(labelA, labelA));
        Assert.assertEquals("14", 0, labelTest(labelB, labelB));

        Assert.assertEquals("15", 0, labelTest(labelC, labelC));
        Assert.assertEquals("16", 0, labelTest(labelD, labelD));
    }

    private void assertNotEqualB(Label labelA, Label labelB, Label labelC, Label labelD) {
        Assert.assertEquals("1", -1, labelTest(labelA, labelB));
        Assert.assertEquals("2", 1, labelTest(labelB, labelA));

        Assert.assertEquals("3", 1, labelTest(labelA, labelC));
        Assert.assertEquals("4", -1, labelTest(labelC, labelA));


        Assert.assertEquals("7", 1, labelTest(labelB, labelC));
        Assert.assertEquals("8", -1, labelTest(labelC, labelB));

        Assert.assertEquals("9", 1, labelTest(labelB, labelD));
        Assert.assertEquals("10", -1, labelTest(labelD, labelB));

        Assert.assertEquals("11", -1, labelTest(labelC, labelD));
        Assert.assertEquals("12", 1, labelTest(labelD, labelC));
    }


    @Test
    public void objectiveValueAndResourcesEqualTwoByTwo() {
        WeightedObjective costA = new WeightedObjective(1);
        WeightedObjective costB = new WeightedObjective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(1, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 1);

        Label labelA = new Label(null, null, 0, costA, resourcesA, 1, 0);
        Label labelB = new Label(null, null, 0, costB, resourcesA, 1, 0);

        Label labelC = new Label(null, null, 0, costA, resourcesB, 1, 0);
        Label labelD = new Label(null, null, 0, costB, resourcesB, 1, 0);
        labelA.setCanLeaveLocationAtTime(2);
        labelB.setCanLeaveLocationAtTime(2);
        labelC.setCanLeaveLocationAtTime(3);
        labelD.setCanLeaveLocationAtTime(3);

        Assert.assertEquals("1", 0, labelTest(labelA, labelB));
        Assert.assertEquals("2", 0, labelTest(labelB, labelA));

        Assert.assertEquals("3", -1, labelTest(labelA, labelC));
        Assert.assertEquals("4", 1, labelTest(labelC, labelA));

        Assert.assertEquals("5", -1, labelTest(labelA, labelD));
        Assert.assertEquals("6", 1, labelTest(labelD, labelA));

        Assert.assertEquals("7", -1, labelTest(labelB, labelC));
        Assert.assertEquals("8", 1, labelTest(labelC, labelB));

        Assert.assertEquals("9", -1, labelTest(labelB, labelD));
        Assert.assertEquals("10", 1, labelTest(labelD, labelB));

        Assert.assertEquals("11", 0, labelTest(labelC, labelD));
        Assert.assertEquals("12", 0, labelTest(labelD, labelC));

    }


    private void assertNotEqualA(Label labelA, Label labelB, Label labelC, Label labelD) {
        Assert.assertEquals("3", -1, labelTest(labelA, labelC));
        Assert.assertEquals("4", 1, labelTest(labelC, labelA));

        Assert.assertEquals("5", -1, labelTest(labelA, labelD));
        Assert.assertEquals("6", 1, labelTest(labelD, labelA));


        Assert.assertEquals("7", -1, labelTest(labelB, labelC));
        Assert.assertEquals("8", 1, labelTest(labelC, labelB));

        Assert.assertEquals("9", -1, labelTest(labelB, labelD));
        Assert.assertEquals("10", 1, labelTest(labelD, labelB));

        Assert.assertEquals("11", -1, labelTest(labelC, labelD));
        Assert.assertEquals("12", 1, labelTest(labelD, labelC));
    }


    private void assertSecondAlwaysDominatesFirst(Label labelA, Label labelB, Label labelC, Label labelD) {
        Assert.assertEquals("3", 2, labelTest(labelA, labelC));
        Assert.assertEquals("4", 2, labelTest(labelC, labelA));

        Assert.assertEquals("5", 2, labelTest(labelA, labelD));
        Assert.assertEquals("6", 2, labelTest(labelD, labelA));

        Assert.assertEquals("7", 2, labelTest(labelB, labelC));
        Assert.assertEquals("8", 2, labelTest(labelC, labelB));

        Assert.assertEquals("9", 2, labelTest(labelB, labelD));
        Assert.assertEquals("10", 2, labelTest(labelD, labelB));
    }

    private void assertEquals(Label labelA, Label labelB, Label labelC, Label labelD) {
        Assert.assertEquals("1", 0, labelTest(labelA, labelA));
        Assert.assertEquals("2", 0, labelTest(labelB, labelB));
        Assert.assertEquals("3", 0, labelTest(labelC, labelC));
        Assert.assertEquals("4", 0, labelTest(labelD, labelD));

        Assert.assertEquals("15", 0, labelTest(labelA, labelA));
        Assert.assertEquals("16", 0, labelTest(labelB, labelB));

        Assert.assertEquals("17", 0, labelTest(labelC, labelC));
        Assert.assertEquals("18", 0, labelTest(labelD, labelD));
    }

    public int labelTest(Label labelA, Label labelB) {
        return labelA.dominates(labelB);
    }
}
