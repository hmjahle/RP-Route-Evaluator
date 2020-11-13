package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.constraintsAndObjectives.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.constraintsAndObjectives.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ExtendToInfo;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.LabellingAlgorithm;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchGraph;
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
        LabellingAlgorithm labellingAlgorithm = new LabellingAlgorithm(graph, new ObjectiveFunctionsIntraRouteHandler(), new ConstraintsIntraRouteHandler());
        Label newLabel = labellingAlgorithm.extendLabelToNextNode(label, new ExtendToInfo(graph.getNode(task), 1));

        Assert.assertNotNull(newLabel);
        Assert.assertEquals("Position should be node: ", "2", newLabel.getNode().toString());
        Assert.assertEquals("Current time should be: ", 2, newLabel.getCurrentTime());
        Assert.assertEquals("Cost should be: ", 0.0, newLabel.getObjective().getObjectiveValue(), 1E-6);
    }

    private SearchGraph buildGraph(ILocation office, Collection<ITask> tasks, ITravelTimeMatrix distanceMatrix) {
        return new SearchGraph(distanceMatrix, tasks, office, office, 0);
    }
}
