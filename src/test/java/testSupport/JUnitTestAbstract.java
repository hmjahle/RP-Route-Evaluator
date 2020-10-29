package testSupport;

import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.Interfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.objectives.Objective;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ResourceOneElement;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchInfo;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestTask;

import java.util.Collection;

public abstract class JUnitTestAbstract {

    protected Label createStartLabel(SearchGraph graph, SearchInfo searchInfo) {
        return new Label(searchInfo, null, graph.getOffice(), graph.getOffice(), null, new Objective(0), 0, 0, new ResourceOneElement(0), 0);
    }

    protected ILocation createOffice() {
        return new TestLocation(true);
    }

    protected SearchGraph buildGraph(ILocation office, Collection<ITask> tasks, ITravelTimeMatrix distanceMatrix) {
        return new SearchGraph(distanceMatrix, tasks, office, 0);
    }

    protected ITask createStandardTask(long duration, long startTime, long endTime) {
        return new TestTask(duration, startTime, endTime, false, false, true, 0, 0, new TestLocation(false),"x");
    }
}