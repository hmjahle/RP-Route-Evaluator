package testSupport;

import com.visma.of.rp.routeevaluator.evaluation.objectives.WeightedObjective;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.Route;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.results.Visit;
import com.visma.of.rp.routeevaluator.solver.algorithm.Label;
import com.visma.of.rp.routeevaluator.solver.algorithm.ResourceOneElement;
import com.visma.of.rp.routeevaluator.solver.algorithm.SearchGraph;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestTask;

import java.util.List;

public abstract class JUnitTestAbstract {

    public static final double DELTA = 1E-6;

    protected int getTime(int hours, int minutes, int seconds) {
        return hours * 3600 + minutes * 60 + seconds;
    }

    protected int getTime(int hours, int minutes) {
        return hours * 3600 + minutes * 60;
    }

    protected int getTime(int hours) {
        return hours * 3600;
    }

    protected Label createStartLabel(SearchGraph graph) {
        return new Label(null, graph.getOrigin(), graph.getOrigin().getLocationId(), new WeightedObjective(0), new ResourceOneElement(0), 0, 0);
    }

    protected ILocation createOffice() {
        return new TestLocation(true);
    }

    protected TestTask createStandardTask(int duration, int startTime, int endTime) {
        return new TestTask(duration, startTime, endTime, false, false, true, 0, 0, new TestLocation(false), "x");
    }

    protected TestTask createStandardTask(int duration, int startTime, int endTime, String id) {
        return new TestTask(duration, startTime, endTime, false, false, true, 0, 0, new TestLocation(false), id);
    }

    protected ITask createSyncedTask(int duration, int startTime, int endTime) {
        return new TestTask(duration, startTime, endTime, false, true, true, 0, 0, new TestLocation(false), "x");
    }

    protected TestTask createSyncedTask(int duration, int startTime, int endTime, String id) {
        return new TestTask(duration, startTime, endTime, false, true, true, 0, 0, new TestLocation(false), id);
    }

    protected TestTask createSyncedStrictTask(int duration, int startTime, int endTime) {
        return new TestTask(duration, startTime, endTime, true, true, true, 0, 0, new TestLocation(false), "x");
    }

    protected TestTask createSyncedStrictTask(int duration, int startTime, int endTime, String id) {
        return new TestTask(duration, startTime, endTime, true, true, true, 0, 0, new TestLocation(false), id);
    }

    protected ITask createStrictTask(int duration, int startTime, int endTime) {
        return new TestTask(duration, startTime, endTime, true, false, true, 0, 0, new TestLocation(false), "x");
    }

    protected TestTask createStrictTask(int duration, int startTime, int endTime, String id) {
        return new TestTask(duration, startTime, endTime, true, false, true, 0, 0, new TestLocation(false), id);
    }

    protected int getVisitTravelTime(RouteEvaluatorResult<ITask> result, int visitNo) {
        Visit<ITask> visitTask1 = result.getVisitSolution().get(visitNo);
        return visitTask1.getTravelTime();
    }

    protected void print(RouteEvaluatorResult result) {
        List<ITask> tasks = result.getRoute().extractEmployeeTasks();
        double objectiveValue = result.getObjectiveValue();
        System.out.println("Objective: " + objectiveValue);
        System.out.println("Route:");
        StringBuilder printRoute = new StringBuilder("\tO");
        for (ITask task : tasks) printRoute.append(" -> ").append(task.getId());
        printRoute.append(" -> D");
        System.out.println(printRoute);
    }

    protected void printRoute(Route<ITask> route) {
        int i = 0;
        for (Visit visit : route.getVisitSolution()) {
            System.out.println("Visit no " + (i++) + " " + printVisit(visit));
        }
}

    protected String printVisit(Visit visit) {
        return "Travel time to: " + visit.getTravelTime() + " start time: " + visit.getStartTime() +
                " \t" + printTask(visit.getTask());

    }


    protected String printTask(ITask task) {
        return "Task id: " + task.getId();
    }
}