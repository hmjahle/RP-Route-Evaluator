package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;


import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implement the INodeExtendSets, it has two elements.
 */
public class ExtendInfoTwoElements implements IExtendInfo {
    NodeList nodeListOne;
    NodeList nodeListTwo;
    List<ExtendToInfo> extendToInfo;

    ExtendInfoTwoElements() {
        extendToInfo = new ArrayList<>();
    }

    ExtendInfoTwoElements(NodeList nodeListOne, NodeList nodeListTwo) {
        extendToInfo = new ArrayList<>();
        update(nodeListOne, nodeListTwo);
    }

    void update(NodeList nodeListOne, NodeList nodeListTwo) {
        this.nodeListOne = nodeListOne;
        this.nodeListTwo = nodeListTwo;
    }

    public List<ExtendToInfo> extend(Label label) {
        ResourceTwoElements resourceTwoElements = (ResourceTwoElements) label.getResources();
        extendToInfo.clear();

        Node node = nodeListOne.getNode(resourceTwoElements.getElementOneCount());
        if (node != null) {
            extendToInfo.add(new ExtendToInfo(node, 1));
        }
        node = nodeListTwo.getNode(resourceTwoElements.getElementTwoCount());
        if (node != null) {
            extendToInfo.add(new ExtendToInfo(node, 2));
        }

        return extendToInfo;
    }

    public IResource createEmptyResource() {
        return new ResourceTwoElements(0, 0);
    }
}
