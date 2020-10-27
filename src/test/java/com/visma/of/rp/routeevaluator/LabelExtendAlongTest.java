package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.Interfaces.IPosition;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ExtendToInfo;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchInfo;
import org.junit.Assert;
import org.junit.Test;
import testInterfaceImplementationClasses.*;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.Collection;

public class LabelExtendAlongTest extends JUnitTestAbstract {

    @Test
    public void singleTask() {
        IPosition office = createOffice();
        ITask task = createStandardTask(1, 2, 5);

        Collection<ITask> tasks = new ArrayList<>();
        tasks.add(task);

        TestDistanceMatrix distanceMatrix = new TestDistanceMatrix();
        distanceMatrix.addUndirectedConnection(office, task.getPosition(), 1);
        SearchGraph graph = buildGraph(office, tasks, distanceMatrix);
        SearchInfo searchInfo = new SearchInfo(graph);

        Label label = createStartLabel(graph, searchInfo);
        Label newLabel = label.extendAlong(new ExtendToInfo(graph.getNode(task), 1));

        Assert.assertNotNull(newLabel);
        Assert.assertEquals("Position should be node: ", "1", newLabel.getPhysicalPosition().toString());
        Assert.assertEquals("Current time should be: ", 2, newLabel.getCurrentTime());
        Assert.assertEquals("Cost should be: ", 0.0, newLabel.getCostFunction().getCost(), 1E-6);
    }

}