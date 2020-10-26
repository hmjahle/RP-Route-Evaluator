package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import java.util.List;

/**
 * The interface is used to find nodes to extend a label to this is returned as a part of the ExtendToInfo along with
 * the node set the node belongs to.
 */
 public interface NodeExtendInfoInterface {

    List<ExtendToInfo> extend(Label label);

    IResource createEmptyResource();
}
