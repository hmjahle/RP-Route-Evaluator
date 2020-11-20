package com.visma.of.rp.routeevaluator.solver.algorithm;

import java.util.Arrays;
import java.util.List;

/**
 * The label lists class is used within the labelling algorithm to handle labels on the node.
 * The class handles the labels on the nodes, when new labels are considered to be added it is checked whether
 * they are dominated by existing labels. If they are, they are not added otherwise they are added.
 * If they dominate other labels those labels are removed from the list.
 */
public class LabelLists {

    private Label[][] elements;
    private int[] elementCnt;
    private int[] labelCapacity;

    public LabelLists(int nodes, int labelCapacity) {
        this.labelCapacity = new int[nodes];
        Arrays.fill(this.labelCapacity, labelCapacity);
        this.elements = new Label[nodes][labelCapacity];
        this.elementCnt = new int[nodes];
    }

    /**
     * Attempts to add a label to a node, if the label is added, i.e., not dominated by and existing
     * label it returns true, otherwise false.
     *
     * @param node  The node to which the label should be added.
     * @param label The label to be added.
     * @return True if the label is added, otherwise false.
     */
    public boolean addLabelOnNode(Node node, Label label) {
        int nodeId = node.getNodeId();
        if (!canBeAdded(label, nodeId))
            return false;
        updateArrayLength(nodeId);
        elements[nodeId][elementCnt[nodeId]++] = label;
        return true;
    }

    /**
     * Check if it is possible to add the label, i.e., if it is dominated.
     * At the same time labels that it might dominate are deleted.
     *
     * @param label  Label to be added.
     * @param nodeId Node to add the label to.
     * @return True if the label can be added to the node, otherwise false.
     */
    private boolean canBeAdded(Label label, int nodeId) {
        int labelCnt = elementCnt[nodeId];
        int i = 0;
        Label currentLabel = elements[nodeId][i];
        while (i < labelCnt) {
            int dominates = currentLabel.dominates(label);
            if (dominates <= 0)
                return false;
            if (dominates == 1) {
                currentLabel.close();
                currentLabel = elements[nodeId][labelCnt - 1];
                elements[nodeId][i] = currentLabel;
                labelCnt--;
            } else {
                currentLabel = ++i < labelCnt ? elements[nodeId][i] : null;
            }
        }
        elementCnt[nodeId] = labelCnt;
        return true;
    }

    /**
     * Check if there is enough capacity in the label list for the given node.
     * If not the array capacity is doubled.
     *
     * @param nodeId Node to check.
     */
    private void updateArrayLength(int nodeId) {
        if (elementCnt[nodeId] >= labelCapacity[nodeId] / 2) {
            labelCapacity[nodeId] *= 2;
            Label[] labels = elements[nodeId];
            elements[nodeId] = new Label[labelCapacity[nodeId]];
            System.arraycopy(labels, 0, elements[nodeId], 0, labels.length);
        }
    }

    public List<Label> findLabels(Node node) {
        return Arrays.asList(elements[node.getNodeId()]).subList(0, elementCnt[node.getNodeId()]);
    }

    public int size(Node node) {
        return elementCnt[node.getNodeId()];
    }

    public void clear() {
        Arrays.fill(elementCnt, 0);
    }

    public int getLabelCapacity(Node node) {
        return labelCapacity[node.getNodeId()];
    }

}
