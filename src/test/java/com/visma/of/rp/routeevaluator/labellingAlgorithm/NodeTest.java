package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.algorithm.Node;
import org.junit.Assert;
import org.junit.Test;
import testSupport.JUnitTestAbstract;

/**
 * Tests the node class as equality, hashCode and toString functions has been overridden.
 */
public class NodeTest extends JUnitTestAbstract {

    @Test
    public void basicEquals() {
        ITask task1 = createStandardTask(1, 1, 2);
        ITask task2 = createStandardTask(1, 1, 2);
        Node nodeA = new Node(1, task1, 0);
        Node nodeB = new Node(1, task1, 1);
        Node nodeC = new Node(1, task2, 2);
        Node nodeD = new Node(2, task2, 3);
        Assert.assertEquals("A and A must be equal", nodeA, nodeA);
        Assert.assertEquals("A and B must be equal", nodeA, nodeB);
        Assert.assertEquals("A and C must be equal", nodeA, nodeC);
        Assert.assertEquals("B and C must be equal", nodeA, nodeC);
        Assert.assertNotEquals("A and D must not be equal", nodeA, nodeD);
        Assert.assertNotEquals("B and D must not be equal", nodeB, nodeD);
        Assert.assertNotEquals("Should not equal other class.",nodeA, 1);
    }

    @Test
    public void basicHashCode() {
        ITask task1 = createStandardTask(1, 1, 2);
        ITask task2 = createStandardTask(1, 1, 2);

        Node nodeA = new Node(1, task1, 0);
        Node nodeB = new Node(2, task2, 1);
        Node nodeC = new Node(100, null, 2);
        Node nodeD = new Node(2, null, 3);

        Assert.assertEquals("A hash code must be: ", 1, nodeA.hashCode());
        Assert.assertEquals("B hash code must be: ", 2, nodeB.hashCode());
        Assert.assertEquals("C hash code must be: ", 100, nodeC.hashCode());
        Assert.assertEquals("D hash code must be: ", 2, nodeD.hashCode());
    }

    @Test
    public void basicToString() {
        ITask task1 = createStandardTask(1, 1, 2);
        ITask task2 = createStandardTask(1, 1, 2);

        Node nodeA = new Node(1, task1, 0);
        Node nodeB = new Node(2, task2, 1);
        Node nodeC = new Node(100, null, 2);
        Node nodeD = new Node(2, null, 3);

        Assert.assertEquals("A string must be: ", "1", nodeA.toString());
        Assert.assertEquals("B string must be: ", "2", nodeB.toString());
        Assert.assertEquals("C string must be: ", "100", nodeC.toString());
        Assert.assertEquals("D string must be: ", "2", nodeD.toString());
    }
}
