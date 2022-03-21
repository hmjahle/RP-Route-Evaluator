package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

    @Mock
    public ITask task;

    @Test
    public void copyConstructorCopiesCorrectly() {
        Node oldNode = new Node(0, task, 12);
        Node nodeCopy = new Node(oldNode);

        Assert.assertEquals(oldNode.getNodeId(), nodeCopy.getNodeId());
        Assert.assertEquals(oldNode.getTask(), nodeCopy.getTask());
        Assert.assertEquals(oldNode.getLocationId(), nodeCopy.getLocationId());
    }

}