package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.LabelQueue;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceTwoElements;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * The label queue is tested. Here both the standard priority queue functionality and the specialized
 * label adaptations are tested.
 */
public class LabelQueueTest {

    /**
     * The priority of labels are tested, the four labels should be returned in the correct order, i.e.,
     * the order in which they dominate each other.
     */
    @Test
    public void labelResourceAndObjectiveValue() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);
        WeightedObjective costC = new WeightedObjective(2);

        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        Label labelA = createLabel(costA, resourcesA);
        Label labelB = createLabel(costB, resourcesA);
        Label labelC = createLabel(costB, resourcesB);
        Label labelD = createLabel(costC, resourcesB);

        LabelQueue labelQueue = new LabelQueue();
        labelQueue.addLabel(labelA);
        labelQueue.addLabel(labelB);
        labelQueue.addLabel(labelC);
        labelQueue.addLabel(labelD);

        Assert.assertEquals("Label A must be the current best label: ", labelQueue.poll(), labelA);
        Assert.assertEquals("Label B must be the current best label: ", labelQueue.poll(), labelB);
        Assert.assertEquals("Label C must be the current best label: ", labelQueue.poll(), labelC);
        Assert.assertEquals("Label D must be the current best label: ", labelQueue.poll(), labelD);
        Assert.assertTrue("The queue must be empty after polling all the elements.", labelQueue.isEmpty());
    }

    /**
     * Tests if the closed labels are moved correctly when possible, 2 closed label case. One of them should always
     * be deleted since the new open label will always be placed as a child node for one of them.
     */
    @Test
    public void removeClosedLabelIfPossible() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);

        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        Label labelA = createLabel(costA, resourcesA);
        Label labelB = createLabel(costB, resourcesA);
        Label labelC = createLabel(costB, resourcesB);
        Label labelD = createLabel(costB, resourcesB);
        LabelQueue labelQueue = new LabelQueue(10);
        labelQueue.addLabel(labelA);
        labelA.close();

        labelQueue.addLabel(labelB);
        labelB.close();
        Assert.assertEquals("The queue must must have 2 elements, as closed labels are only removed when the queue size is more than 2.", 2, labelQueue.size());

        labelQueue.addLabel(labelC);
        labelC.close();
        Assert.assertEquals("The queue must must have 3 elements, as closed labels are only removed when the queue size is more than 2.", 3, labelQueue.size());
        labelQueue.addLabel(labelD);

        Assert.assertEquals("The queue must must have 1 element, label D should remove label A, B and C when added as the queue now has more than 2 elements.", 1, labelQueue.size());

        Assert.assertEquals("Label D must be the current best label: ", labelQueue.poll(), labelD);
        Assert.assertTrue("The queue must be empty after polling the last element.", labelQueue.isEmpty());
    }

    /**
     * Tests if the clear functionality works by adding labels, clearing adding and polling again to see if there
     * can be polled more labels than there should be in the queue.
     */
    @Test
    public void clear1() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);

        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            labels.add(createLabel(costA, resourcesA));
            labels.add(createLabel(costB, resourcesB));
            labels.add(createLabel(costA, resourcesB));
            labels.add(createLabel(costB, resourcesA));
        }

        LabelQueue labelQueue = new LabelQueue();
        for (int i = 0; i < 40; i++)
            labelQueue.addLabel(labels.get(i));

        Assert.assertEquals("The queue must must have 40 elements.", 40, labelQueue.size());
        labelQueue.clear();
        Assert.assertTrue("The queue must be empty after clearing the queue.", labelQueue.isEmpty());

        for (int i = 0; i < 10; i++)
            labelQueue.addLabel(labels.get(i));
        Assert.assertEquals("The queue must must have 10 elements.", 10, labelQueue.size());

        int labelsFromQueue = 0;
        while (labelQueue.poll() != null)
            labelsFromQueue++;
        Assert.assertEquals("Should only be possible to poll 10 times as there should only be 10 " +
                "elements on the queue.", 10, labelsFromQueue);
        Assert.assertTrue("The queue must be empty after polling all the elements.", labelQueue.isEmpty());
    }

    /**
     * Tests if the clear functionality works by adding labels, clearing adding and polling
     * to see if the correct labels are returned.
     */
    @Test
    public void clear2() {
        WeightedObjective costA = new WeightedObjective(0);
        WeightedObjective costB = new WeightedObjective(1);

        ResourceTwoElements resourcesA = new ResourceTwoElements(2, 2);
        ResourceTwoElements resourcesB = new ResourceTwoElements(1, 2);

        List<Label> labelsA = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            labelsA.add(createLabel(costA, resourcesA));
        }
        List<Label> labelsB = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            labelsB.add(createLabel(costB, resourcesB));
        }
        LabelQueue labelQueue = new LabelQueue();
        for (int i = 0; i < 10; i++)
            labelQueue.addLabel(labelsB.get(i));

        Assert.assertEquals("The queue must must have 10 elements.", 10, labelQueue.size());
        labelQueue.clear();
        Assert.assertTrue("The queue must be empty after clearing the queue.", labelQueue.isEmpty());

        for (int i = 0; i < 10; i++)
            labelQueue.addLabel(labelsA.get(i));
        Assert.assertEquals("The queue must must have 10 elements.", 10, labelQueue.size());

        Label labelPoll = labelQueue.poll();
        int labelsFromQueue = 0;
        while (labelPoll != null) {
            Assert.assertEquals("Label must be with objective A, all with objective B should have been cleared from the queue.",
                    costA.getObjectiveValue(), labelPoll.getObjective().getObjectiveValue(), 1E-6);
            labelPoll = labelQueue.poll();
            labelsFromQueue++;
        }
        Assert.assertEquals("Should only be possible to poll 10 times as there should only be 10 " +
                "elements on the queue.", 10, labelsFromQueue);
        Assert.assertTrue("The queue must be empty after polling all the elements.", labelQueue.isEmpty());
    }


    private Label createLabel(WeightedObjective cost1, ResourceTwoElements resourcesA) {
        return new Label(null, null, 0,
                cost1, resourcesA, 3, 0);
    }

}
