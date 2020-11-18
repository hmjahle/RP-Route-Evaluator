package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.LabelObjectiveValueComparer;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.List;

import static benchmarking.benchmarking.printResult;

public class LabelFunctionTest extends JUnitTestAbstract {

    Label labelA;
    Label labelB;
    Label labelC;
    Label labelD;
    LabelObjectiveValueComparer comparer;

    @Before
    public void initialize() {

        comparer = new LabelObjectiveValueComparer();
        WeightedObjective costA = new WeightedObjective(1);
        WeightedObjective costB = new WeightedObjective(1);
        WeightedObjective costD = new WeightedObjective(0);
        ResourceTwoElements resourcesA = new ResourceTwoElements(1, 1);
        ResourceTwoElements resourcesC = new ResourceTwoElements(1, 2);
        ResourceTwoElements resourcesD = new ResourceTwoElements(1, 0);

        labelA = new Label(null, null, 0, costA, resourcesA, 2, 0);
        labelB = new Label(null, null, 0, costA, resourcesA, 2, 0);
        labelC = new Label(null, null, 0, costB, resourcesC, 2, 0);
        labelD = new Label(null, null, 0, costD, resourcesD, 1, 0);
        labelA.setCanLeaveLocationAtTime(2);
        labelB.setCanLeaveLocationAtTime(2);
        labelC.setCanLeaveLocationAtTime(2);
        labelD.setCanLeaveLocationAtTime(3);
    }

    /**
     * The equals function is not overridden so labels should only be equal if they are the same object.
     * Label A and B has equal values but should not be equal.
     */
    @Test
    public void equals() {

        Assert.assertEquals("Label A should equal label A", labelA, labelA);
        Assert.assertEquals("Label B should equal label B", labelB, labelB);


        Assert.assertNotEquals("Label A should equal label A", labelA, labelB);
        Assert.assertNotEquals("Label B should equal label B", labelB, labelA);

        Assert.assertNotEquals("Label A should equal label C", labelA, labelC);
        Assert.assertNotEquals("Label C should equal  label A", labelC, labelA);

    }

    @Test
    public void compareTo() {

        Assert.assertEquals("Label A should equal label B when compared", 0, comparer.compare(labelA, labelB));
        Assert.assertEquals("Label B should equal label A when compared", 0, comparer.compare(labelB, labelA));

        Assert.assertEquals("Label A is equal label C when compared", 0, comparer.compare(labelA, labelC));
        Assert.assertEquals("Label C is equal label A when compared", 0, comparer.compare(labelC, labelA));

        Assert.assertEquals("Label A is worse than label D when compared", 1, comparer.compare(labelA, labelD));
        Assert.assertEquals("Label D is better than label A when compared", -1, comparer.compare(labelD, labelA));
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
