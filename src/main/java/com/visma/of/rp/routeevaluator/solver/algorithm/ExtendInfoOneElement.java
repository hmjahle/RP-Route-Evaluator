package com.visma.of.rp.routeevaluator.solver.algorithm;

import java.util.Enumeration;

/**
 * This class implement the IExtendInfo, it has two elements.
 */
public class ExtendInfoOneElement implements IExtendInfo {

    NodeList nodeListOne;
    Node node = null;

    public ExtendInfoOneElement(NodeList nodeListOne) {
        this.nodeListOne = nodeListOne;
    }

    @Override
    public Enumeration<ExtendToInfo> extend(Label label) {
        node = nodeListOne.getNode(((ResourceOneElement) label.getResources()).getElementOneCount());

        return new ExtendInfoOneElementEnumerator();

    }

    @Override
    public IResource createEmptyResource() {
        return new ResourceOneElement(0);
    }


    private class ExtendInfoOneElementEnumerator implements Enumeration<ExtendToInfo> {

        boolean next = (node != null);

        @Override
        public boolean hasMoreElements() {
            return next;
        }

        @Override
        public ExtendToInfo nextElement() {
            next = false;
            return new ExtendToInfo(node, 1);
        }

    }
}
