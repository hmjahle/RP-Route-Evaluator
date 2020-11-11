package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchGraph;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ExtendToInfo;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.LabellingAlgorithm;
import org.junit.Assert;
import org.junit.Test;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.Collection;

public class LabelExtendAlongTest extends JUnitTestAbstract {

    @Test
    public void singleTask() {
        ILocation office = createOffice();
        ITask task = createStandardTask(1, 2, 5);

        Collection<ITask> tasks = new ArrayList<>();
        tasks.add(task);

        TestTravelTimeMatrix distanceMatrix = new TestTravelTimeMatrix();
        distanceMatrix.addUndirectedConnection(office, task.getLocation(), 1);
        SearchGraph graph = buildGraph(office, tasks, distanceMatrix);

        Label label = createStartLabel(graph);
        LabellingAlgorithm labellingAlgorithm = new LabellingAlgorithm(graph);
        Label newLabel = labellingAlgorithm.extendLabelToNextNode(label, new ExtendToInfo(graph.getNode(task), 1));

        Assert.assertNotNull(newLabel);
        Assert.assertEquals("Position should be node: ", "2", newLabel.getCurrentLocation().toString());
        Assert.assertEquals("Current time should be: ", 2, newLabel.getCurrentTime());
        Assert.assertEquals("Cost should be: ", 0.0, newLabel.getObjective().getObjectiveValue(), 1E-6);
    }

}
