package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.searchGraph.Node;
import com.visma.of.rp.routeevaluator.searchGraph.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implement the INodeExtendSets, it has two elements.
 */
public class NodeExtendInfoTwoElements implements NodeExtendInfoInterface {
    NodeList nodeListOne;
    NodeList nodeListTwo;
    List<ExtendToInfo> extendToInfo;

    NodeExtendInfoTwoElements() {
        extendToInfo = new ArrayList<>();
    }

    NodeExtendInfoTwoElements(NodeList nodeListOne, NodeList nodeListTwo) {
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
