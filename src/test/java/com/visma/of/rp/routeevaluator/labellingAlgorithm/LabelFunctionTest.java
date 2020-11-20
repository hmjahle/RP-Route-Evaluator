package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.Node;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.List;

public class LabelFunctionTest extends JUnitTestAbstract {

    Label labelA;
    Label labelB;
    Label labelC;
    Label labelD;

    @Before
    public void initialize() {

        WeightedObjective costA = new WeightedObjective(1);
        WeightedObjective costB = new WeightedObjective(1);
        WeightedObjective costC = new WeightedObjective(1);
        WeightedObjective costD = new WeightedObjective(0);
        ResourceTwoElements resourcesA = new ResourceTwoElements(1, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 1);
        ResourceTwoElements resourcesC = new ResourceTwoElements(1, 2);
        ResourceTwoElements resourcesD = new ResourceTwoElements(1, 0);

        labelA = new Label(null, null, 0, costA, resourcesA, 2, 0);
        labelB = new Label(null, null, 0, costC, resourcesB, 2, 0);
        labelC = new Label(null, null, 0, costB, resourcesC, 2, 0);
        labelD = new Label(null, null, 0, costD, resourcesD, 1, 0);
        labelA.setCanLeaveLocationAtTime(2);
        labelB.setCanLeaveLocationAtTime(2);
        labelC.setCanLeaveLocationAtTime(2);
        labelD.setCanLeaveLocationAtTime(3);
    }

    @Test
    public void equals() {

        Assert.assertEquals("Label A should equal label B", labelA, labelB);
        Assert.assertEquals("Label B should equal label A", labelB, labelA);

        Assert.assertNotEquals("Label A should not equal label C", labelA, labelC);
        Assert.assertNotEquals("Label C should not equal label A", labelC, labelA);

        Assert.assertNotEquals("Label A should not equal label D", labelA, labelD);
        Assert.assertNotEquals("Label D should not equal label A", labelD, labelA);

        Assert.assertNotEquals("Should not equal other class.", labelA, 1);
    }

    @Test
    public void compareTo() {

        Assert.assertEquals("Label A should equal label B when compared", 0, labelA.compareTo(labelB));
        Assert.assertEquals("Label B should equal label A when compared", 0, labelB.compareTo(labelA));

        Assert.assertEquals("Label A is equal label C when compared", 0, labelA.compareTo(labelC));
        Assert.assertEquals("Label C is equal label A when compared", 0, labelC.compareTo(labelA));

        Assert.assertEquals("Label A is worse than label D when compared", 1, labelA.compareTo(labelD));
        Assert.assertEquals("Label D is better than label A when compared", -1, labelD.compareTo(labelA));
    }

    @Test
    public void dominates() {

        Assert.assertEquals("Label A are equal with respect to domination label B", 0, labelA.dominates(labelB));
        Assert.assertEquals("Label B are equal with respect to domination label A", 0, labelB.dominates(labelA));

        Assert.assertEquals("Label A is dominated by label C", 1, labelA.dominates(labelC));
        Assert.assertEquals("Label C dominates label A", -1, labelC.dominates(labelA));

        Assert.assertEquals("Label A and label D does not dominate each other", 2, labelA.dominates(labelD));
        Assert.assertEquals("Label D and label A does not dominate each other", 2, labelD.dominates(labelA));
    }

    @Test
    public void string() {

        Assert.assertNotNull("Should print string. ", labelA.toString());
        Assert.assertNotNull("Should print string. ", labelB.toString());
        Assert.assertNotNull("Should print string. ", labelC.toString());
        Assert.assertNotNull("Should print string. ", labelD.toString());

        Label label = new Label(null, null, 0, null, null, 0, 0);
        Assert.assertEquals("'Empty' label should print. ", "-1, 0.0", label.toString());

        label = new Label(null, new Node(3, null, 7), 0,
                new WeightedObjective(5.0), null, 0, 0);
        Assert.assertEquals("Label should print. ", "3, 5.0", label.toString());

    }

    @Test
    public void overriddenHashCode() {

        Label label = new Label(null, null, 0, null, null, 0, 0);
        Assert.assertEquals("'Empty' label should have hash code of 0. ", 0, label.hashCode());

        WeightedObjective obj = new WeightedObjective(5);
        ResourceTwoElements res = new ResourceTwoElements(1, 1);
        Node node = new Node(3, null, 7);
        label = new Label(null, node, node.getLocationId(), obj, res, 2, 4);
        label.setCanLeaveLocationAtTime(2);
        int hash = (res.hashCode() // resource hash code.
                + 2 //Current time.
                + 2 * (int) Math.pow(2, 2)  //canLeaveLocationAtTime.
                + 4 * (int) Math.pow(2, 4) //travelTime.
                + 3 * (int) Math.pow(2, 6) //node id.
                + 7 * (int) Math.pow(2, 8) //location id.
                + 5 * (int) Math.pow(2, 10)); //objectiveValue.
        Assert.assertEquals("Hash code should be correct. ", hash, label.hashCode());
    }

    @Test
    public void closed() {

        List<Label> labels = new ArrayList<>();
        labels.add(labelA);
        labels.add(labelB);
        labels.add(labelC);
        labels.add(labelD);
        long closedLabels;

        closedLabels = labels.stream().filter(Label::isClosed).count();
        Assert.assertEquals("Should be zero closed labels:", 0, closedLabels);

        labelA.close();
        closedLabels = labels.stream().filter(Label::isClosed).count();
        Assert.assertEquals("Should be one closed labels:", 1, closedLabels);

        labelD.close();
        closedLabels = labels.stream().filter(Label::isClosed).count();
        Assert.assertEquals("Should be two closed labels:", 2, closedLabels);

        labels.add(new Label(null, null, 0, null, null, 0, 0));
        closedLabels = labels.stream().filter(Label::isClosed).count();
        Assert.assertEquals("Should be two closed labels:", 2, closedLabels);
    }

}
