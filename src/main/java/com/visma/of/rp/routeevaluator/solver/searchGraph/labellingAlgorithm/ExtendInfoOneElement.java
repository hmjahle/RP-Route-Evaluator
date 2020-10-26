package com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm;



import com.visma.of.rp.routeevaluator.solver.searchGraph.Node;
import com.visma.of.rp.routeevaluator.solver.searchGraph.NodeList;


import java.util.ArrayList;
import java.util.List;

/**
 * This class implement the INodeExtendSets, it has two elements.
 */
public class ExtendInfoOneElement implements IExtendInfo {
    NodeList nodeListOne;
    List<ExtendToInfo> extendToInfo;

    public ExtendInfoOneElement() {
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
