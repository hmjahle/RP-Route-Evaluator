package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.searchGraph.Node;
import com.visma.of.rp.routeevaluator.searchGraph.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implement the INodeExtendSets, it has two elements.
 */
public class NodeExtendInfoOneElement implements NodeExtendInfoInterface {
    NodeList nodeListOne;
    List<ExtendToInfo> extendToInfo;

    public NodeExtendInfoOneElement() {
        extendToInfo = new ArrayList<>();
    }

    public void update(NodeList nodeListOne) {
        this.nodeListOne = nodeListOne;
    }

    public List<ExtendToInfo> extend(Label label) {
        ResourceOneElement resourceOneElement = (ResourceOneElement) label.getResources();
        extendToInfo.clear();
        Node node = nodeListOne.getNode(resourceOneElement.getElementOneCount());
        if (node != null) {
            extendToInfo.add(new ExtendToInfo(node, 1));
        }
        return extendToInfo;
    }

    public IResource createEmptyResource() {
        return new ResourceOneElement(0);
    }
}
