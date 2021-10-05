package com.visma.of.rp.routeevaluator.results;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Route<T extends ITask> {

    private final List<Visit<T>> visitSolution;
    private int routeFinishedAtTime;

    public Route() {
        this.visitSolution = new ArrayList<>();
        this.routeFinishedAtTime = 0;
    }

    public void addVisits(List<Visit<T>> visits) {
        visitSolution.addAll(visits);
    }

    public void setRouteFinishedAtTime(int routeFinishedAtTime) {
        this.routeFinishedAtTime = routeFinishedAtTime;
    }

    public int getRouteFinishedAtTime() {
        return routeFinishedAtTime;
    }

    public List<Visit<T>> getVisitSolution() {
        return visitSolution;
    }

    /**
     * Extracts the tasks from the route, they are returned in the correct order.
     *
     * @return List of tasks, can be empty.
     */
    public List<T> extractEmployeeTasks() {
        return this.getVisitSolution().stream().map(i -> ((T) i.getTask())).collect(Collectors.toList());
    }

    /**
     * Extracts the visits, where the task is synced, from the route,
     * they are returned as a set and hence no order is guaranteed.
     *
     * @return The set of visits, can be empty.
     */
    public Set<Visit<T>> extractSyncedVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isSynced()).collect(Collectors.toSet());
    }

    /**
     * Extracts the visits, where the task is strict, from the route,
     * they are returned as a set and hence no order is guaranteed.
     *
     * @return The set of visits, can be empty.
     */
    public Set<Visit<T>> extractStrictVisits() {
        return this.getVisitSolution().stream().filter(i -> i.getTask().isStrict()).collect(Collectors.toSet());
    }

    /**
     * Finds the position of the task in the route.
     *
     * @param task Task to find the position for.
     * @return Integer position, null if the task is not in the route.
     */
    public Integer findIndexInRoute(T task) {
        int i = 0;
        for (Visit<T> visit : visitSolution)
            if (visit.getTask() == task)
                return i;
            else i++;
        return null;
    }
}
