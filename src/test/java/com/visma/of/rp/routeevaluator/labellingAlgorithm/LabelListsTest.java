package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.LabelLists;
import com.visma.of.rp.routeevaluator.solver.algorithm.Node;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LabelListsTest {

    @Test
    public void labelDominanceResourceAndObjectiveValue() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);

        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        Label labelA = createLabel(costA, resourcesA);
        Label labelB = createLabel(costB, resourcesA);
        Label labelC = createLabel(costA, resourcesB);
        Label labelD = createLabel(costB, resourcesB);

        LabelLists list = new LabelLists(10, 10);
        Node node = new Node(1, null, 1);

        List<Label> labels = new ArrayList<>();
        labels.add(labelD);
        labels.add(labelB);
        labels.add(labelC);
        labels.add(labelA);

        for (Label label : labels) {
            list.addLabelOnNode(node, label);
        }

        Assert.assertEquals("Size", 1, list.findLabels(node).size());
        for (int i = 0; i < list.findLabels(node).size(); i++) {
            Label labelActual = list.findLabels(node).get(i);
            Assert.assertEquals("Labels match", labelActual, labelA);
        }
    }

    @Test
    public void labelsOutOfBound() {
        WeightedObjective cost1 = new WeightedObjective(0);
        WeightedObjective cost2 = new WeightedObjective(1);
        WeightedObjective cost3 = new WeightedObjective(2);
        WeightedObjective cost4 = new WeightedObjective(3);



        ResourceTwoElements resourcesA = new ResourceTwoElements(0, 1);
        ResourceTwoElements resourcesB = new ResourceTwoElements(0, 2);
        ResourceTwoElements resourcesC = new ResourceTwoElements(0, 3);
        ResourceTwoElements resourcesD = new ResourceTwoElements(0, 4);
        ResourceTwoElements resourcesE = new ResourceTwoElements(1, 0);
        ResourceTwoElements resourcesF = new ResourceTwoElements(2, 0);
        ResourceTwoElements resourcesG = new ResourceTwoElements(3, 0);
        ResourceTwoElements resourcesH = new ResourceTwoElements(4, 0);

        Label labelA = createLabel(cost1, resourcesA);
        Label labelB = createLabel(cost2, resourcesB);
        Label labelC = createLabel(cost3, resourcesC);
        Label labelD = createLabel(cost4, resourcesD);
        Label labelE = createLabel(cost1, resourcesE);
        Label labelF = createLabel(cost2, resourcesF);
        Label labelG = createLabel(cost3, resourcesG);
        Label labelH = createLabel(cost4, resourcesH);

        LabelLists list = new LabelLists(10, 4);
        Node node = new Node(1, null, 1);
        Assert.assertEquals("List length wrong", 0, list.size(node));
        Assert.assertEquals("List capacity wrong", 4, list.getLabelCapacity(node));

        list.addLabelOnNode(node, labelA);
        list.addLabelOnNode(node, labelB);
        Assert.assertEquals("List length wrong", 2, list.size(node));
        Assert.assertEquals("List capacity wrong", 4, list.getLabelCapacity(node));

        list.addLabelOnNode(node, labelC);
        Assert.assertEquals("List length wrong", 3, list.size(node));
        Assert.assertEquals("List capacity wrong", 4, list.getLabelCapacity(node));
        list.addLabelOnNode(node, labelD);
        list.addLabelOnNode(node, labelE);
        Assert.assertEquals("List length wrong", 5, list.size(node));
        Assert.assertEquals("List capacity wrong", 12, list.getLabelCapacity(node));

        list.addLabelOnNode(node, labelF);
        list.addLabelOnNode(node, labelG);
        list.addLabelOnNode(node, labelH);
        Assert.assertEquals("List length wrong", 8, list.size(node));
        Assert.assertEquals("List capacity wrong", 12, list.getLabelCapacity(node));

        list.addLabelOnNode(node, labelA);
        list.addLabelOnNode(node, labelB);
        list.addLabelOnNode(node, labelC);
        list.addLabelOnNode(node, labelD);
        list.addLabelOnNode(node, labelE);
        list.addLabelOnNode(node, labelF);
        list.addLabelOnNode(node, labelG);
        list.addLabelOnNode(node, labelH);
        Assert.assertEquals("List length wrong", 8, list.size(node));
        Assert.assertEquals("List capacity wrong", 12, list.getLabelCapacity(node));
    }

    private Label createLabel(WeightedObjective cost1, ResourceTwoElements resourcesA) {
        return new Label(null, null, 0,
                cost1, resourcesA, 3, 0);
    }

}
