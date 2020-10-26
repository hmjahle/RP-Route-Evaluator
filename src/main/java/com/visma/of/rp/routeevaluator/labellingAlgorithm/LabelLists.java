package com.visma.of.rp.routeevaluator.searchGraph.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.searchGraph.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public boolean addAndReturnTrueIfAdded(Node node, Label label) {
        int nodeId = node.getId();
        List<Integer> labelsDominated = new ArrayList<>();
        if (isDominated(label, nodeId, labelsDominated))
            return false;
        addLabel(label, nodeId, labelsDominated);
        return true;
    }

    public void addLabel(Label label, int nodeId, List<Integer> labelsDominated) {
        if (labelsDominated.isEmpty()) {
            updateArrayLength(nodeId);
            elements[nodeId][elementCnt[nodeId]++] = label;
        } else {
            elements[nodeId][labelsDominated.get(0)] = label;
            for (int i = 1; i < labelsDominated.size(); i++) {
                elements[nodeId][labelsDominated.get(i)] = elements[nodeId][elementCnt[nodeId] - 1];
                elementCnt[nodeId]--;
            }
        }
    }

    public boolean isDominated(Label label, int nodeId, List<Integer> labelsDominated) {
        for (int i = 0; i < elementCnt[nodeId]; i++) {
            int dominates = elements[nodeId][i].dominates(label);
            if (dominates <= 0)
                return true;
            if (dominates == 1) {
                labelsDominated.add(i);
                elements[nodeId][i].setClosed(true);
            }
        }
        return false;
    }

    private void updateArrayLength(int nodeId) {
        if (elementCnt[nodeId] >= labelCapacity[nodeId] / 2) {
            labelCapacity[nodeId] *= 2;
            Label[] labels = elements[nodeId];
            elements[nodeId] = new Label[labelCapacity[nodeId]];
            System.arraycopy(labels, 0, elements[nodeId], 0, labels.length);
        }
    }

    public List<Label> findLabels(Node node) {
        return Arrays.asList(elements[node.getId()]).subList(0, elementCnt[node.getId()]);
    }

    public int size(Node node) {
        return elementCnt[node.getId()];
    }

    public void clear() {
        Arrays.fill(elementCnt, 0);
    }

    public int getLabelCapacity(Node node) {
        return labelCapacity[node.getId()];
    }
}
