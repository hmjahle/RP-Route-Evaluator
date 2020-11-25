package com.visma.of.rp.routeevaluator.solver.algorithm;

import java.util.Enumeration;

/**
 * The interface is used to find nodes to extend a label to this is returned as a part of the ExtendToInfo along with
 * the node set the node belongs to.
 */
  interface IExtendInfo {

    Enumeration<ExtendToInfo> extend(Label label);

    IResource createEmptyResource();
}
