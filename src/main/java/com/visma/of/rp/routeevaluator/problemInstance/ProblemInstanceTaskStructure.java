package com.visma.of.rp.routeevaluator.problemInstance;

import aws.errors.ErrorResponseSaver;
import aws.errors.detail.validation.MultipleIdentifierForTaskToRequiredEmployeeError;
import aws.errors.parent.DataValidationError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import inputhandler.entityhelpers.GeneralTaskHelper;
import inputhandler.entityhelpers.PatientTaskHelper;
import inputhandler.entityhelpers.SyncedTaskPairHelper;
import probleminstance.entities.interfaces.EntityInterface;
import probleminstance.entities.restrictions.TaskToRequiredEmployees;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.EmployeeTask;
import probleminstance.entities.timeinterval.task.GeneralTask;
import probleminstance.entities.timeinterval.task.PatientTask;
import probleminstance.entities.timeinterval.task.Task;
import utils.comparator.CompareMethods;
import utils.files.JSON;

import javax.validation.ConstraintViolation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProblemInstanceTaskStructure {


    /***
     * All tasks contains all tasks part of ProblemInstance
     * If a task exists in tasksForSearch, it should be present in the other structures.
     * Other structures should be consistent with allTasks.
     */

    @JsonIgnore
    private ProblemInstance problemInstance;

    @JsonIgnore
    private Map<String, Task> allTasks;
    @JsonIgnore
    private Map<String, Task> tasksForSearch;

    @JsonIgnore
    private Map<String, Set<Task>> originalTaskIdsToTasks;
    @JsonIgnore
    private Map<String, PatientTask> patientTasks;
    @JsonIgnore
    private Map<String, EmployeeTask> employeeTasks;
    @JsonIgnore
    private Map<String, GeneralTask> generalTasks;
    @JsonIgnore
    private Map<String, Set<Task>> patientToTasks;
    @JsonIgnore
    private Map<EmployeeWorkShift, Set<Task>> employeeWorkShiftToEmployeeTasks;
    @JsonIgnore
    private Map<String, Set<EmployeeWorkShift>> originalEmployeeTaskIdToEmployeeWorkShifts;
    @JsonIgnore
    private List<Task> originalSearchHeavyTasksCompetenceSorted;
    @JsonIgnore
    private Map<String, TaskToRequiredEmployees> originalTaskIdToTaskToRequiredEmployees;

    ProblemInstanceTaskStructure(ProblemInstance problemInstance) {
        this.problemInstance = problemInstance;
        initializeStructures();
    }

    public void initializeStructures() {
        allTasks = new HashMap<>();
        tasksForSearch = new HashMap<>();
        originalTaskIdsToTasks = new HashMap<>();
        patientTasks = new HashMap<>();
        employeeTasks = new HashMap<>();
        generalTasks = new HashMap<>();
        patientToTasks = new HashMap<>();
        employeeWorkShiftToEmployeeTasks = new HashMap<>();
        originalEmployeeTaskIdToEmployeeWorkShifts = new HashMap<>();
        originalTaskIdToTaskToRequiredEmployees = new HashMap<>();
    }

    Task getTask(String taskId) {
        return allTasks.get(taskId);
    }


    void setPatientTasks(ProblemInstance problemInstance, List<PatientTask> patientTasksRaw, Set<ConstraintViolation<EntityInterface>> violations) {
        patientTasks = PatientTaskHelper.createMultipleTasksBasedOnMultipleEmployees(
                JSON.generateObjectMap(patientTasksRaw, violations, problemInstance, problemInstance.getConfiguration()), problemInstance
        );
        if (violations.isEmpty()) {
            setUpHelpStructureFor(patientTasks, PatientTask::getPatientId, patientToTasks);
            PatientTaskHelper.checkThatPatientForPatientTaskExist(problemInstance.getPatients().keySet(), patientTasks.values(), problemInstance.getErrorAppender());
            PatientTaskHelper.removePatientIfNoPatientTasks(problemInstance);
            allTasks.putAll(patientTasks);
            tasksForSearch.putAll(patientTasks);
            updateOriginalTaskIdToTaskIds(patientTasks.values());
        }
    }


    void setEmployeeTasks(ProblemInstance problemInstance, List<EmployeeTask> employeeTasksRaw, Set<ConstraintViolation<EntityInterface>> violations) {
        employeeTasks = JSON.generateObjectMap(employeeTasksRaw, violations, problemInstance, problemInstance.getConfiguration());
        employeeTasks = employeeTasks.entrySet().stream().filter(e -> problemInstance.getEmployeeStructure().getOriginalEmployeeIds().
                contains(e.getValue().getOriginalEmployeeId())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        setUpHelpStructureFor(employeeTasks, task -> problemInstance.getEmployeeStructure().getEmployeeWorkShift(task.getWorkShiftId()), employeeWorkShiftToEmployeeTasks);
        updateOriginalTaskIdToTaskIds(employeeTasks.values());
    }


    void setGeneralTasks(ProblemInstance problemInstance, List<GeneralTask> generalTasksRaw, Set<ConstraintViolation<EntityInterface>> violations) {
        generalTasks = JSON.generateObjectMap(generalTasksRaw, violations, problemInstance, problemInstance.getConfiguration());
        allTasks.putAll(generalTasks);
        for (EmployeeWorkShift employeeId : employeeWorkShiftToEmployeeTasks.keySet()) {
            for (Task task : employeeWorkShiftToEmployeeTasks.get(employeeId)) {
                allTasks.put(task.getTaskId(), employeeTasks.get(task.getTaskId()));
                tasksForSearch.put(task.getTaskId(), employeeTasks.get(task.getTaskId()));
            }
        }
    }

    void updateHeavyTaskSortedList() {
        originalSearchHeavyTasksCompetenceSorted = tasksForSearch.values()
                .stream()
                .filter(Task::isHeavy)
                .sorted(CompareMethods.getSkillLevelComparatorAscending().reversed())
                .collect(Collectors.toList());
    }

    void setUpOriginalTaskIdToTaskToRequiredEmployee() {
        for (TaskToRequiredEmployees taskToRequiredEmployees : problemInstance.getTaskToReqEmployeesRaw()) {
            if (!originalEmployeeTaskIdToEmployeeWorkShifts.containsKey(taskToRequiredEmployees.getTaskId())) {
                originalTaskIdToTaskToRequiredEmployees.put(taskToRequiredEmployees.getTaskId(), taskToRequiredEmployees);
            } else {
                ErrorResponseSaver.saveError(new DataValidationError(), new MultipleIdentifierForTaskToRequiredEmployeeError(taskToRequiredEmployees.getUniqueId()),
                        problemInstance.getErrorAppender(), new Exception());
            }
        }
    }

    private <T extends Task, K> void setUpHelpStructureFor(Map<String, T> taskMap, Function<T, K> keyFunction,
                                                        Map<K, Set<Task>> mapToFill) {
        for (T task : taskMap.values()) {
            K key = keyFunction.apply(task);
            if(key != null){
                mapToFill.computeIfAbsent(key, k -> new HashSet<>());
                mapToFill.get(key).add(task);
            }
        }
    }


    public void assignEmployeeTaskToEmployee(EmployeeWorkShift employeeWorkShift, EmployeeTask employeeTask) {
        employeeWorkShiftToEmployeeTasks.computeIfAbsent(employeeWorkShift, k -> new HashSet<>());
        employeeWorkShiftToEmployeeTasks.get(employeeWorkShift).add(employeeTask);
        originalEmployeeTaskIdToEmployeeWorkShifts.computeIfAbsent(employeeTask.getOriginalTaskId(), k -> new HashSet<>());
        originalEmployeeTaskIdToEmployeeWorkShifts.get(employeeTask.getOriginalTaskId()).add(employeeWorkShift);
        addEmployeeTask(employeeTask);
    }

    public void addEmployeeTask(EmployeeTask employeeTask) {
        updateOriginalTaskIdToTaskIds(employeeTask);
        employeeTasks.put(employeeTask.getTaskId(), employeeTask);
        allTasks.put(employeeTask.getTaskId(), employeeTask);
        tasksForSearch.put(employeeTask.getTaskId(), employeeTask);
    }


    void updateOriginalTaskIdToTaskIds(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            updateOriginalTaskIdToTaskIds(task);
        }
    }

    private <T extends Task> void updateOriginalTaskIdToTaskIds(T task) {
        originalTaskIdsToTasks.putIfAbsent(task.getOriginalTaskId(), new HashSet<>());
        originalTaskIdsToTasks.get(task.getOriginalTaskId()).add(task);
    }

    public void setUpSyncedTasks() {
        SyncedTaskPairHelper.validateSyncedTaskPairs(problemInstance);
        if (!problemInstance.getErrorAppender().hasError()) {
            setUpSyncedPatientTasks();
            setUpSyncedEmployeeTasks();
        }
    }

    private void setUpSyncedPatientTasks() {
        if (problemInstance.getSyncedTaskPairs() == null) {
            PatientTaskHelper.findAndUpdateSyncedTasksThatIsDefinedAsMultipleTasks(problemInstance);
        } else {
            SyncedTaskPairHelper.updateSyncedTasks(problemInstance.getSyncedTaskPairs(), originalTaskIdsToTasks::get);
        }
    }

    private void setUpSyncedEmployeeTasks() {
        GeneralTaskHelper.updateSynchronizedEmployeeTasks(employeeTasks.values());
    }

    public Collection<Task> getTasksForSearch() {
        return tasksForSearch.values();
    }

    public Set<Task> getEmployeeTasksFor(EmployeeWorkShift employeeWorkShift) {
        return employeeWorkShiftToEmployeeTasks.get(employeeWorkShift);
    }

    public Collection<GeneralTask> getGeneralTasks() {
        return generalTasks.values();
    }

    public Collection<EmployeeTask> findEmployeeTasksInSearch() {
        List<EmployeeTask> employeeTasks = new ArrayList<>();
        for(Task task : getTasksForSearch()){
            if(task instanceof  EmployeeTask){
                employeeTasks.add((EmployeeTask) task);
            }
        }
        return employeeTasks;
    }



    public Collection<Task> getAllTasks() {
        return allTasks.values();
    }

    public Collection<PatientTask> getPatientTasks() {
        return patientTasks.values();
    }

    public List<Task> getOriginalSearchHeavyTasksCompetenceSorted() {
        return originalSearchHeavyTasksCompetenceSorted;
    }

    public boolean patientTasksContains(Task task) {
        return patientTasks.containsKey(task.getTaskId());
    }

    public boolean patientTasksContains(String task) {
        return patientTasks.containsKey(task);
    }

    public boolean tasksForSearchContains(Task task) {
        return tasksForSearch.containsKey(task.getTaskId());
    }

    public boolean tasksForSearchContains(String task) {
        return tasksForSearch.containsKey(task);
    }

    public boolean employeeTasksContains(Task task) {
        return employeeTasks.containsKey(task.getTaskId());
    }

    public boolean employeeTasksContains(String taskId) {
        return employeeTasks.containsKey(taskId);
    }

    public Set<EmployeeWorkShift> getEmployeeWorkShiftsForOriginalEmployeeTaskId(String originalEmployeeTaskId) {
        return originalEmployeeTaskIdToEmployeeWorkShifts.get(originalEmployeeTaskId);
    }

    public Collection<Task> getAllAssignedEmployeeTasks() {
        Collection<Task> allEmployeeAssignedTasks = new ArrayList<>();
        employeeWorkShiftToEmployeeTasks.values().forEach(allEmployeeAssignedTasks::addAll);
        return allEmployeeAssignedTasks;
    }

    public Collection<Task> getTasksForPatient(String patientId) {
        if (patientToTasks.get(patientId) == null) {
            return new ArrayList<>();
        } else return patientToTasks.get(patientId);
    }

    public Set<Task> getTasksForOriginalTaskId(String originalTask) {
        return  originalTaskIdsToTasks.get(originalTask);
    }

    public boolean containsOriginalTaskId(String originalTaskId) {
        return originalTaskIdsToTasks.containsKey(originalTaskId);
    }

    public Map<String, TaskToRequiredEmployees> getOriginalTaskIdToTaskToRequiredEmployees() {
        return originalTaskIdToTaskToRequiredEmployees;
    }

    public void removeTaskFromSearch(Task task) {
        String addressEntityId = allTasks.get(task.getTaskId()).getAddressEntity().getId();
        tasksForSearch.remove(task.getTaskId());
        generalTasks.remove(task.getTaskId());
        if (patientToTasks.get(addressEntityId) != null) {
            patientToTasks.get(addressEntityId).remove(task);
            patientTasks.remove(task.getTaskId());
            if (patientToTasks.get(addressEntityId).isEmpty()) {
                patientToTasks.remove(addressEntityId);
                problemInstance.removePatient(addressEntityId);
            }
        }

    }

    public int getNumberOfTasks() {
        return allTasks.size();
    }

}
