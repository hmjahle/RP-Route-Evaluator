package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.intraRouteEvaluation.objectives.Objective;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests whether the labelling dominance rules are implemented correctly.
 */
public class LabelDominanceTest {


    @Test
    public void resourceAndObjectiveValue() {
        Objective costA = new Objective(0);
        Objective costB = new Objective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);
        Label labelA = new Label(null, null, null, null, costA, resourcesA, 3, 0, 2);
        Label labelB = new Label(null, null, null, null, costB, resourcesA, 3, 0, 2);
        Label labelC = new Label(null, null, null, null, costA, resourcesB, 3, 0, 2);
        Label labelD = new Label(null, null, null, null, costB, resourcesB, 3, 0, 2);

        Assert.assertEquals("1", 0, labelTest(labelA, labelA));
        Assert.assertEquals("2", 0, labelTest(labelB, labelB));
        Assert.assertEquals("3", 0, labelTest(labelC, labelC));
        Assert.assertEquals("4", 0, labelTest(labelD, labelD));

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

        Assert.assertEquals("15", 0, labelTest(labelA, labelA));
        Assert.assertEquals("16", 0, labelTest(labelB, labelB));

        Assert.assertEquals("17", 0, labelTest(labelC, labelC));
        Assert.assertEquals("18", 0, labelTest(labelD, labelD));

    }

    @Test
    public void currentTimeAndDrivingTime() {
        Objective costA = new Objective(0);
        ResourceTwoElements resources = new ResourceTwoElements(0, 0);

        Label labelA = new Label(null, null, null, null, costA, resources, 1, 0, 2);
        Label labelB = new Label(null, null, null, null, costA, resources, 2, 0, 0);
        Label labelC = new Label(null, null, null, null, costA, resources, 3, 0, 3);
        Label labelD = new Label(null, null, null, null, costA, resources, 4, 0, 4);

        Assert.assertEquals("1", 2, labelTest(labelA, labelB));
        Assert.assertEquals("2", 2, labelTest(labelB, labelA));

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

        Assert.assertEquals("13", 0, labelTest(labelA, labelA));
        Assert.assertEquals("14", 0, labelTest(labelB, labelB));

        Assert.assertEquals("15", 0, labelTest(labelC, labelC));
        Assert.assertEquals("16", 0, labelTest(labelD, labelD));

    }

    @Test
    public void currentTimeAndDrivingTimeAndObjectiveValue() {
        Objective costA = new Objective(0);
        Objective costB = new Objective(1);
        ResourceTwoElements resources = new ResourceTwoElements(0, 0);

        Label labelA = new Label(null, null, null, null, costA, resources, 1, 0, 1);
        Label labelB = new Label(null, null, null, null, costA, resources, 2, 0, 2);
        Label labelC = new Label(null, null, null, null, costB, resources, 1, 0, 1);
        Label labelD = new Label(null, null, null, null, costB, resources, 2, 0, 2);


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
        Objective costA = new Objective(0);
        Objective costB = new Objective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(0, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 0);

        Label labelA = new Label(null, null, null, null, costA, resourcesA, 1, 0, 1);
        Label labelB = new Label(null, null, null, null, costA, resourcesA, 2, 0, 2);

        Label labelC = new Label(null, null, null, null, costB, resourcesB, 1, 0, 1);
        Label labelD = new Label(null, null, null, null, costB, resourcesB, 2, 0, 2);


        Assert.assertEquals("1", -1, labelTest(labelA, labelB));
        Assert.assertEquals("2", 1, labelTest(labelB, labelA));

        Assert.assertEquals("3", 2, labelTest(labelA, labelC));
        Assert.assertEquals("4", 2, labelTest(labelC, labelA));

        Assert.assertEquals("5", 2, labelTest(labelA, labelD));
        Assert.assertEquals("6", 2, labelTest(labelD, labelA));

        Assert.assertEquals("7", 2, labelTest(labelB, labelC));
        Assert.assertEquals("8", 2, labelTest(labelC, labelB));

        Assert.assertEquals("9", 2, labelTest(labelB, labelD));
        Assert.assertEquals("10", 2, labelTest(labelD, labelB));

        Assert.assertEquals("11", -1, labelTest(labelC, labelD));
        Assert.assertEquals("12", 1, labelTest(labelD, labelC));

        Assert.assertEquals("13", 0, labelTest(labelA, labelA));
        Assert.assertEquals("14", 0, labelTest(labelB, labelB));

        Assert.assertEquals("15", 0, labelTest(labelC, labelC));
        Assert.assertEquals("16", 0, labelTest(labelD, labelD));
    }

    @Test
    public void objectiveValueAndResources() {
        Objective costA = new Objective(0);
        Objective costB = new Objective(1);
        ResourceTwoElements resourcesA = new ResourceTwoElements(1, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        Label labelA = new Label(null, null, null, null, costA, resourcesA, 1, 0, 0);
        Label labelB = new Label(null, null, null, null, costB, resourcesA, 1, 0, 0);

        Label labelC = new Label(null, null, null, null, costA, resourcesB, 1, 0, 0);
        Label labelD = new Label(null, null, null, null, costB, resourcesB, 1, 0, 0);


        Assert.assertEquals("1", -1, labelTest(labelA, labelB));
        Assert.assertEquals("2", 1, labelTest(labelB, labelA));

        Assert.assertEquals("3", 1, labelTest(labelA, labelC));
        Assert.assertEquals("4", -1, labelTest(labelC, labelA));

        Assert.assertEquals("5", 2, labelTest(labelA, labelD));
        Assert.assertEquals("6", 2, labelTest(labelD, labelA));

        Assert.assertEquals("7", 1, labelTest(labelB, labelC));
        Assert.assertEquals("8", -1, labelTest(labelC, labelB));

        Assert.assertEquals("9", 1, labelTest(labelB, labelD));
        Assert.assertEquals("10", -1, labelTest(labelD, labelB));

        Assert.assertEquals("11", -1, labelTest(labelC, labelD));
        Assert.assertEquals("12", 1, labelTest(labelD, labelC));

        Assert.assertEquals("13", 0, labelTest(labelA, labelA));
        Assert.assertEquals("14", 0, labelTest(labelB, labelB));

        Assert.assertEquals("15", 0, labelTest(labelC, labelC));
        Assert.assertEquals("16", 0, labelTest(labelD, labelD));
    }

    public int labelTest(Label a, Label b) {
        Label labelA = new Label(null, a.getPrevious(), a.getNode(), a.getCurrentLocation(), a.getObjective(), a.getResources(), a.getCurrentTime(), 0, a.getCanLeaveLocationAtTime());
        Label labelB = new Label(null, b.getPrevious(), b.getNode(), b.getCurrentLocation(), b.getObjective(), b.getResources(), b.getCurrentTime(), 0, b.getCanLeaveLocationAtTime());
        return labelA.dominates(labelB);
    }
}
