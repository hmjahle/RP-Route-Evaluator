package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Route {

    private List<Visit> visitSolution;
    private long timeOfArrivalAtDestination;

    public Route() {
        this.visitSolution = new ArrayList<>();
        this.timeOfArrivalAtDestination = 0L;
    }

    public void addVisits(Visit[] visits, int visitsCnt) {
        visitSolution.addAll(Arrays.asList(visits).subList(0, visitsCnt));
    }

    public void setTimeOfArrivalAtDestination(long timeOfArrivalAtDestination) {
        this.timeOfArrivalAtDestination = timeOfArrivalAtDestination;
    }

    public long getTimeOfArrivalAtDestination() {
        return timeOfArrivalAtDestination;
    }

    public List<Visit> getVisitSolution() {
        return visitSolution;
    }

    /**
     * Extracts the tasks from the route, they are returned in the correct order.
     *
     * @return List of tasks, can be empty.
     */
    public List<ITask> extractEmployeeTasks() {
        return this.getVisitSolution().stream().map(Visit::getTask).collect(Collectors.toList());
    }

    /**
     * Extracts the visits, where the task is synced, from the route,
     * they are returned as a set and hence no order is guaranteed.
     *
     * @return The set of visits, can be empty.
     */
    public Set<Visit> extractSyncedVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isSynced()).collect(Collectors.toSet());
    }

    /**
     * Extracts the visits, where the task is strict, from the route,
     * they are returned as a set and hence no order is guaranteed.
     *
     * @return The set of visits, can be empty.
     */
    public Set<Visit> extractStrictVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isStrict()).collect(Collectors.toSet());
    }
}
