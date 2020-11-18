package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.solver.algorithm.NodeList;
import com.visma.of.rp.routeevaluator.solver.algorithm.SearchGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests the node list class that is used within the labelling algorithm.
 * The class uses an array to store nodes from a search graph. The array is not changed.
 * During use hence logic around over writing old nodes and keeping track of data in the
 * list is tested here.
 */
public class NodeListsTest extends JUnitTestAbstract {

    ILocation office;
    List<ITask> allTasks;
    ITravelTimeMatrix travelTimeMatrix;
    SearchGraph graph;

    @Before
    public void initialize() {
        office = createOffice();
        allTasks = createTasks();
        travelTimeMatrix = createTravelTimeMatrix(office, allTasks);
        graph = new SearchGraph(travelTimeMatrix, allTasks, office, office, 0);
    }

    @Test
    public void basicConstruction() {
        NodeList nodeList = new NodeList(10);

        Assert.assertNull("First element should be null. ", nodeList.getNode(0));
        Assert.assertEquals("List should be empty. ", 0, nodeList.size());
    }

    @Test
    public void initializationSingleNode() {
        NodeList nodeList = new NodeList(10);
        nodeList.initializeWithNode(graph, allTasks.get(0));

        Assert.assertNotNull("First element should be a node. ", nodeList.getNode(0));
        Assert.assertNull("Second element should be null. ", nodeList.getNode(1));
        Assert.assertEquals("List should have one element. ", 1, nodeList.size());
    }

    @Test
    public void initializationMultipleNodes() {
        NodeList nodeList = new NodeList(10);
        nodeList.initializeWithNodes(graph, allTasks);

        Assert.assertNotNull("First element should be a node. ", nodeList.getNode(0));
        Assert.assertNull("Ninth element should be null. ", nodeList.getNode(9));
        Assert.assertEquals("List should have 8 elements. ", 8, nodeList.size());

        nodeList.clear();
        Assert.assertEquals("List should have 0 elements. ", 0, nodeList.size());
        Assert.assertNull("First element should be null. ", nodeList.getNode(0));
    }

    @Test
    public void constructAndAddNodes() {
        NodeList nodeList = new NodeList(10);

        Assert.assertNull("First element should be null. ", nodeList.getNode(0));
        Assert.assertEquals("List should be empty. ", 0, nodeList.size());

        nodeList.addNode(graph, allTasks.get(5));
        Assert.assertEquals("List should have 1 elements. ", 1, nodeList.size());
        Assert.assertNull("Second element should be null. ", nodeList.getNode(1));
        Assert.assertEquals("First element should be task. ", "6", nodeList.getNode(0).getTask().getId());
        nodeList.addNode(graph, allTasks.get(0));
        nodeList.addNode(graph, allTasks.get(1));
        nodeList.addNode(graph, allTasks.get(2));
        nodeList.addNode(graph, allTasks.get(3));
        Assert.assertEquals("List should have 5 elements. ", 5, nodeList.size());

        nodeList.clear();
        Assert.assertEquals("List should have 0 elements. ", 0, nodeList.size());

        nodeList.addNode(graph, allTasks.get(0));
        Assert.assertEquals("First element should be task. ", "1", nodeList.getNode(0).getTask().getId());
    }

    @Test
    public void confirmTasks() {
        NodeList nodeList = new NodeList(10);
        nodeList.initializeWithNodes(graph, allTasks);

        for (int i = 0; i < nodeList.size(); i++)
            Assert.assertEquals("Task should be equal: ", allTasks.get(i).getId(), nodeList.getNode(i).getTask().getId());
    }

    private ITravelTimeMatrix createTravelTimeMatrix(ILocation office, Collection<ITask> tasks) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, taskA.getLocation(), 2);
            for (ITask taskB : tasks)
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 1);
        }
        return travelTimeMatrix;
    }

    private List<ITask> createTasks() {
        ITask taskA = createStandardTask(1, 10, 50, "1");
        ITask taskB = createStandardTask(1, 20, 60, "2");
        ITask taskC = createStandardTask(1, 30, 70, "3");
        ITask taskD = createStandardTask(1, 40, 80, "4");
        ITask taskE = createStandardTask(1, 50, 90, "5");
        ITask taskF = createStandardTask(1, 30, 70, "6");
        ITask taskG = createStandardTask(1, 30, 70, "7");
        ITask taskH = createStandardTask(1, 40, 80, "8");

        List<ITask> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);
        tasks.add(taskE);
        tasks.add(taskF);
        tasks.add(taskG);
        tasks.add(taskH);
        return tasks;
    }
}
