package com.visma.of.rp.routeevaluator.employeefitness;

import aws.entity.responses.routesinformation.TaskIdInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import probleminstance.entities.timeinterval.task.Task;
import routeplanner.individual.Individual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NonAllocatedTasksIndividual {

    @JsonProperty
    private List<TaskIdInfo> allNonAllocatedTasksList;

    @JsonProperty
    private List<String> nonAllocatedTasksSearchList;

    @JsonProperty
    private List<String> nonAllocatedTasksSearchListWithSearchIds;

    public NonAllocatedTasksIndividual() {
    }

    public NonAllocatedTasksIndividual(Individual individual) {
        allNonAllocatedTasksList = new ArrayList<>();
        allNonAllocatedTasksList.addAll(individual.getProblemInstance().getProblemInstanceHelpStructure()
                .getNonAllocatedFeasibilityCheck().getNonAllocatedTasksFromFeasibilityCheck().stream()
                .map(TaskIdInfo::new)
                .collect(Collectors.toList())
        );
        nonAllocatedTasksSearchList = new ArrayList<>();
        nonAllocatedTasksSearchListWithSearchIds = new ArrayList<>();
    }

    public NonAllocatedTasksIndividual(NonAllocatedTasksIndividual copy) {
        allNonAllocatedTasksList = new ArrayList<>(copy.allNonAllocatedTasksList);
        nonAllocatedTasksSearchList = new ArrayList<>(copy.nonAllocatedTasksSearchList);
        nonAllocatedTasksSearchListWithSearchIds = new ArrayList<>(copy.nonAllocatedTasksSearchListWithSearchIds);
    }

    public void addNonAllocatedTaskFromSearch(Task task) {
        allNonAllocatedTasksList.add(new TaskIdInfo(task));
        nonAllocatedTasksSearchList.add(task.getOriginalTaskId());
        nonAllocatedTasksSearchListWithSearchIds.add(task.getTaskId());
    }

    public void addNonAllocatedTasksFromSearch(Collection<Task> tasks) {
        for (Task task : tasks)
            addNonAllocatedTaskFromSearch(task);
    }

    public List<TaskIdInfo> getAllNonAllocatedTasksList() {
        return allNonAllocatedTasksList;
    }

    public List<String> getNonAllocatedTasksSearchList() {
        return nonAllocatedTasksSearchList;
    }

    public List<String> getNonAllocatedTasksSearchListWithSearchIds() {
        return nonAllocatedTasksSearchListWithSearchIds;
    }

    @JsonIgnore
    public int getTotalNumberOfNonAllocatedTasks() {
        return allNonAllocatedTasksList.size();
    }

    @JsonIgnore
    public int getNumberOfNonAllocatedFromSearch() {
        return nonAllocatedTasksSearchList.size();
    }

    @JsonIgnore
    public void removeTask(Task task) {
        allNonAllocatedTasksList.remove(new TaskIdInfo(task));
        nonAllocatedTasksSearchList.remove(task.getOriginalTaskId());
        nonAllocatedTasksSearchListWithSearchIds.remove(task.getTaskId());
    }

    @Override
    public String toString() {
        return "NonAllocatedTasksIndividual{" +
                "allNonAllocatedTasksList=" + allNonAllocatedTasksList +
                ", nonAllocatedTasksSearchList=" + nonAllocatedTasksSearchList +
                '}';
    }

}
