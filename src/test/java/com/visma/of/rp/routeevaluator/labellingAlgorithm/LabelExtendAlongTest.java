package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.evaluation.constraints.ConstraintsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.evaluation.objectives.ObjectiveFunctionsIntraRouteHandler;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.solver.algorithm.ExtendToInfo;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.LabellingAlgorithm;
import com.visma.of.rp.routeevaluator.solver.algorithm.SearchGraph;
import org.junit.Assert;
import org.junit.Test;
import testInterfaceImplementationClasses.TestShift;
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
        labellingAlgorithm.setEmployeeWorkShift(new TestShift(0, 100));
        Label newLabel = labellingAlgorithm.extendLabelToNextNode(label, new ExtendToInfo(graph.getNode(task), 1));

        Assert.assertNotNull(newLabel);
        Assert.assertEquals("Position should be node: ", "2", newLabel.getNode().toString());
        Assert.assertEquals("Current time should be: ", 2, newLabel.getCurrentTime());
        Assert.assertEquals("Cost should be: ", 0.0, newLabel.getObjective().getObjectiveValue(), 1E-6);
        Assert.assertEquals("Shift start time should be: ", 0, newLabel.getShiftStartTime());
    }

    private SearchGraph buildGraph(ILocation office, Collection<ITask> tasks, ITravelTimeMatrix distanceMatrix) {
        return new SearchGraph(distanceMatrix, tasks, office, office);
    }
}
