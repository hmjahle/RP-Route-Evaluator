package testSupport;

import com.visma.of.rp.routeevaluator.publicInterfaces.ILocation;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITask;
import com.visma.of.rp.routeevaluator.publicInterfaces.ITravelTimeMatrix;
import com.visma.of.rp.routeevaluator.objectives.Objective;
import com.visma.of.rp.routeevaluator.routeResult.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.routeResult.Visit;
import com.visma.of.rp.routeevaluator.solver.searchGraph.SearchGraph;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.Label;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.ResourceOneElement;
import com.visma.of.rp.routeevaluator.solver.searchGraph.labellingAlgorithm.SearchInfo;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestTask;

import java.util.Collection;

public abstract class JUnitTestAbstract {

    protected long getTime(long hours, long minutes, long seconds) {
        return hours * 3600 + minutes * 60 + seconds;
    }

    protected long getTime(long hours, long minutes) {
        return hours * 3600 + minutes * 60;
    }

    protected long getTime(long hours) {
        return hours * 3600;
    }

    protected Label createStartLabel(SearchGraph graph, SearchInfo searchInfo) {
        return new Label(searchInfo, null, graph.getOffice(), graph.getOffice(),  new Objective(0),  new ResourceOneElement(0), 0,0,0);
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

    protected ITask createSyncedTask(long duration, long startTime, long endTime) {
        return new TestTask(duration, startTime, endTime, false, true, true, 0, 0, new TestLocation(false),"x");
    }

    protected ITask createStrictTask(long duration, long startTime, long endTime) {
        return new TestTask(duration, startTime, endTime, true, false, true, 0, 0, new TestLocation(false),"x");
    }

    protected long getVisitTravelTime(RouteEvaluatorResult result, int visitNo) {
        Visit visitTask1 = result.getVisitSolution().get(visitNo);
        return visitTask1.getTravelTimeWithParking();
    }

}