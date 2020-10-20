package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implement the INodeExtendSets, it has two elements.
 */
public class NodeExtendInfoOneElement implements NodeExtendInfoInterface {
    NodeList nodeListOne;
    List<ExtendToInfo> extendToInfo;

    NodeExtendInfoOneElement() {
        extendToInfo = new ArrayList<>();
    }

    void update(NodeList nodeListOne) {
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

    public ResourceInterface createEmptyResource() {
        return new ResourceOneElement(0);
    }
}
