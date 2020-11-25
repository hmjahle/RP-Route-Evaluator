package com.visma.of.rp.routeevaluator.solver.algorithm;


import java.util.Enumeration;

/**
 * This class implement the IExtendInfo, it has two elements.
 */
public class ExtendInfoTwoElements implements IExtendInfo {

    NodeList nodeListOne;
    NodeList nodeListTwo;
    ExtendToInfo[] nodeInfo = new ExtendToInfo[2];
    int size;

    public ExtendInfoTwoElements(NodeList nodeListOne, NodeList nodeListTwo) {
        this.nodeListOne = nodeListOne;
        this.nodeListTwo = nodeListTwo;
    }

    @Override
    public Enumeration<ExtendToInfo> extend(Label label) {
        ResourceTwoElements resourceTwoElements = (ResourceTwoElements) label.getResources();
        Node nodeOne = nodeListOne.getNode(resourceTwoElements.getElementOneCount());
        Node nodeTwo = nodeListTwo.getNode(resourceTwoElements.getElementTwoCount());
        if (nodeOne != null) {
            nodeInfo[0] = new ExtendToInfo(nodeOne, 1);
            size = 1;
        } else {
            size = 0;
        }
        if (nodeTwo != null) {
            nodeInfo[size++] = new ExtendToInfo(nodeTwo, 2);
        }

        return new ExtendInfoTwoElementsEnumerator();
    }


    @Override
    public IResource createEmptyResource() {
        return new ResourceTwoElements(0, 0);
    }


    private class ExtendInfoTwoElementsEnumerator implements Enumeration<ExtendToInfo> {

        int next;

        @Override
        public boolean hasMoreElements() {
            return next != size;
        }

        @Override
        public ExtendToInfo nextElement() {
            return nodeInfo[next++];
        }
    }
}
