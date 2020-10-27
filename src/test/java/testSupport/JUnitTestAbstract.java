package testSupport;

import com.visma.of.rp.routeevaluator.Interfaces.IDistanceMatrix;
import com.visma.of.rp.routeevaluator.Interfaces.IPosition;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.costFunctions.CostFunction;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ResourceOneElement;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchInfo;
import com.visma.of.rp.routeevaluator.transportInfo.TransportMode;
import testInterfaceImplementationClasses.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class JUnitTestAbstract {

    protected Label createStartLabel(SearchGraph graph, SearchInfo searchInfo) {
        return new Label(searchInfo, null, graph.getOffice(), graph.getOffice(), null, new CostFunction(0), 0, 0, new ResourceOneElement(0), 0);
    }

    protected IPosition createOffice() {
        return new TestPosition(true);
    }

    protected SearchGraph buildGraph(IPosition office, Collection<ITask> tasks, IDistanceMatrix distanceMatrix) {
        Map<TransportMode, IDistanceMatrix> distanceMatrices = new HashMap<>();
        distanceMatrices.put(TransportMode.DRIVE, distanceMatrix);
        SearchGraph graph = new SearchGraph(distanceMatrices, tasks, office, 0);
        graph.setEdgesTransportMode(TransportMode.DRIVE);
        return graph;
    }

    protected ITask createStandardTask(long duration, long startTime, long endTime) {
        return new TestTask(duration, startTime, endTime, false, false, true, 0, 0, new TestPosition(false));
    }
}