package com.visma.of.rp.routeevaluator.problemInstance;

import aws.entity.ErrorAppender;
import aws.entity.responses.updateprobleminstance.UpdateProblemInstanceResponse;
import feasibilitychecker.heavytaskchecker.HeavyTaskChecker;
import feasibilitychecker.synccapacity.SyncedTasksCapacityInitializer;
import feasibilitychecker.taskfeasibility.entities.infeasibilitytype.InfeasibilityType;
import inputhandler.entityhelpers.EmployeeTaskHelper;
import inputhandler.entityhelpers.PatientTaskHelper;
import inputhandler.validation.compatibility.TaskToCompatibleEmployeesValidation;
import probleminstance.entities.NonAllocatedFeasibilityCheck;
import probleminstance.entities.address.AddressEntity;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import probleminstance.entities.transport.TransportInformation;
import probleminstance.initializers.employee.TaskToCompatibleEmployeeInitialization;
import probleminstance.initializers.restrictions.TaskVisitRestrictionsCreator;
import probleminstance.initializers.tasktorequiredemployees.TaskToRequiredEmployeesInitializer;
import probleminstance.initializers.transport.TransportInitialization;
import probleminstance.initializers.visithistory.VisitHistoryFitnessCreator;
import utils.copy.HashMapUtilities;
import utils.maps.coordinates.AddressGenerator;
import utils.maps.coordinates.CoordinateValidator;

import java.util.*;
import java.util.stream.Collectors;

public class ProblemInstanceHelpStructure {

    private boolean shouldUseTransportInformation;
    private Map<Task, Set<EmployeeWorkShift>> taskToCompatibleEmployeesWorkShifts;
    private TransportInformation transportInformation;
    private List<EmployeeWorkShift> employeeWorkShiftList;
    private Map<EmployeeWorkShift, Integer> employeeIdsToIndex;
    private VisitHistoryFitnessCreator visitHistoryFitnessCreator;

    private Map<Task, EmployeeWorkShift> taskToInitialEmployeeWorkShiftAssignment;
    private List<Task> syncedTasks;
    private List<Task> syncedTasksTimeChangeable;
    private List<Task> syncedTasksEmployeeChangeable;
    private List<Task> singleTasks;
    private Set<Task> strictTasks;

    private HeavyTaskChecker heavyTaskChecker;
    private ProblemInstance problemInstance;
    private ErrorAppender errorAppender;

    private NonAllocatedFeasibilityCheck nonAllocatedFeasibilityCheck;
    private Map<String, Set<InfeasibilityType>> originalTaskIdToInfeasibilityTypes;

    /**
     * Contains additional data structures for simpler look-ups during the search
     */

    ProblemInstanceHelpStructure(ProblemInstance problemInstance, ErrorAppender errorAppender, boolean shouldUseTransportInformation) {
        initialize(problemInstance, errorAppender);
        this.shouldUseTransportInformation = shouldUseTransportInformation;
    }

    ProblemInstanceHelpStructure(ProblemInstance problemInstance, ErrorAppender errorAppender, TransportInformation transportInformation) {
        initialize(problemInstance, errorAppender);
        this.transportInformation = transportInformation;
        this.shouldUseTransportInformation = true;
    }

    private void initialize(ProblemInstance problemInstance, ErrorAppender errorAppender) {
        this.problemInstance = problemInstance;
        this.errorAppender = errorAppender;
        originalTaskIdToInfeasibilityTypes = new HashMap<>();
        nonAllocatedFeasibilityCheck = new NonAllocatedFeasibilityCheck();
        taskToCompatibleEmployeesWorkShifts = new HashMap<>();
    }

    void setUpHelpStructures() {
        updateTaskToCompatibleEmployees(new HashSet<>(problemInstance.getProblemInstanceTaskStructure().getTasksForSearch()));
        removeTasksWithoutValidAddress();
        if (!getErrorAppender().hasError()) {
            initializeTransportInformation();
            if (!getErrorAppender().hasError()) {
                employeeHelpStructureInitialization();
                setUpVisitHistory();
                setUpBasicVisitRestrictions();
                setUpTaskToRequiredEmployeesStructure();
                taskToInitialEmployeeWorkShiftAssignment = SyncedTasksCapacityInitializer.updateCompatibilityForSyncedTasksAndReturnInitialAssignment(problemInstance);
                setUpTaskStructures();
            }
            problemInstance.setTaskWorkShiftCompatibilityList(new ArrayList<>());
            UpdateProblemInstanceResponse.updateCompatibilityList(problemInstance.getTaskWorkShiftCompatibilityList(), problemInstance);
        }
    }

    private void removeTasksWithoutValidAddress() {
        AddressGenerator.getCoordinatesForPatients(problemInstance.getOfficeInfo(), problemInstance.getPatients().values(),
                problemInstance.getConfiguration(), errorAppender, problemInstance.getDynamoDBUtils());
        Set<Task> tasksWithoutAddress = PatientTaskHelper.removeTasksWhenPatientAddressIsNotFoundAndReturnRelevantTasks(problemInstance);
        updateTaskToCompatibleEmployees(tasksWithoutAddress);
        validateCoordinates();
    }

    private void validateCoordinates() {
        List<AddressEntity> addressEntities = new ArrayList<>(problemInstance.getPatients().values());
        addressEntities.add(problemInstance.getOfficeInfo());
        if (!problemInstance.getOfficeInfo().getUseEuclideanDistancesForTravelInformation())
            CoordinateValidator.validateAddressEntities(addressEntities, errorAppender);
    }

    private void employeeHelpStructureInitialization() {
        employeeWorkShiftList = new ArrayList<>(problemInstance.getEmployeeStructure().getActiveEmployeeWorkShifts());
        employeeIdsToIndex = HashMapUtilities.getIndexMapFromList(employeeWorkShiftList);
    }

    private void initializeTransportInformation() {
        if (shouldUseTransportInformation && transportInformation == null) {
            if (problemInstance.getOfficeInfo().getUseEuclideanDistancesForTravelInformation()) {
                transportInformation = TransportInitialization.updateTransportInformationWithTravelTimesUsingEuclideanDistances(problemInstance, errorAppender);
            } else {
                transportInformation = TransportInitialization.updateTransportInformationWithTravelTimes(problemInstance, errorAppender);
            }
        }

    }

    private void setUpVisitHistory() {
        if (problemInstance.getConfiguration().getVisitHistoryWeight() > 0) {
            visitHistoryFitnessCreator = new VisitHistoryFitnessCreator(
                    problemInstance.getVisitHistoryEmployeePatientPairs().values(),
                    problemInstance.getPatients().keySet(),
                    problemInstance.getEmployeeStructure().getOriginalEmployeeIds(),
                    problemInstance.getOfficeInfo().getNumberOfMainResponsiblesInVisitHistory(),
                    problemInstance.getConfiguration().getVisitHistoryLowerPenalty(),
                    problemInstance.getConfiguration().getVisitHistoryUpperPenalty(),
                    problemInstance.getConfiguration().getVisitHistoryNewEmployeePenalty()
            );
        }
    }

    private void setUpBasicVisitRestrictions() {
        TaskVisitRestrictionsCreator.initialize(problemInstance);
        EmployeeTaskHelper.addVisitRestrictionsAndUpdateListIncompatibleTasks(problemInstance);
        updateTaskToCompatibleEmployees(new HashSet<>(problemInstance.getProblemInstanceTaskStructure().getTasksForSearch()));
    }

    private void setUpTaskStructures() {
        syncedTasks = new ArrayList<>();
        singleTasks = new ArrayList<>();
        strictTasks = new HashSet<>();
        for (Task task : problemInstance.getProblemInstanceTaskStructure().getTasksForSearch()) {
            if (task.isSynced()) {
                syncedTasks.add(task);
            } else {
                singleTasks.add(task);
            }
            if (task.isStrict()) {
                strictTasks.add(task);
            }
        }
        setSyncedTasksHelpStructures();
        setUpHeavyTasks();
    }

    private void setSyncedTasksHelpStructures() {
        syncedTasksTimeChangeable = syncedTasks.stream().filter(
                task -> !task.hasNoSlack() && !task.isDependent()
        ).collect(Collectors.toList());
        syncedTasksEmployeeChangeable = syncedTasks.stream().filter(task ->
                taskToCompatibleEmployeesWorkShifts.get(task).size() > 1).collect(Collectors.toList());
    }

    public void setUpHeavyTasks() {
        heavyTaskChecker = new HeavyTaskChecker(problemInstance);
        if (problemInstance.getConsiderHeavyTasks()) {
            heavyTaskChecker.doInitialCheck();
        }
    }

    public void updateTaskToCompatibleEmployees(Set<Task> tasksToUpdate) {
        TaskToCompatibleEmployeeInitialization.updateTaskToCompatibleEmployees(taskToCompatibleEmployeesWorkShifts,
                problemInstance.getVisitRestrictions(), tasksToUpdate, problemInstance.getEmployeeStructure().getActiveEmployeeWorkShifts(),
                originalTaskIdToInfeasibilityTypes);
        TaskToCompatibleEmployeesValidation.validateAndRemoveCompatibleEmployees(taskToCompatibleEmployeesWorkShifts, tasksToUpdate, problemInstance);
    }


    private void setUpTaskToRequiredEmployeesStructure() {
        TaskToRequiredEmployeesInitializer.run(problemInstance);
        updateTaskToCompatibleEmployees(new HashSet<>(problemInstance.getProblemInstanceTaskStructure().getTasksForSearch()));
    }

    public Map<Task, Set<EmployeeWorkShift>> getTaskToCompatibleEmployeesWorkShifts() {
        return taskToCompatibleEmployeesWorkShifts;
    }

    public boolean employeeCompatibleWithTask(EmployeeWorkShift employeeWorkShift, Task task) {
        return taskToCompatibleEmployeesWorkShifts.get(task).contains(employeeWorkShift);
    }

    public Map<String, Set<EmployeeWorkShift>> convertTaskToCompatibleEmployeesToTaskIds(Map<Task, Set<EmployeeWorkShift>> taskToCompatibleEmployees) {
        Map<String, Set<EmployeeWorkShift>> taskIdsCompatibleEmployees = new HashMap<>();
        for (Map.Entry<Task, Set<EmployeeWorkShift>> kvp : taskToCompatibleEmployees.entrySet())
            taskIdsCompatibleEmployees.put(kvp.getKey().getTaskId(), new HashSet<>(kvp.getValue()));
        return taskIdsCompatibleEmployees;
    }

    public Map<String, Set<EmployeeWorkShift>> convertTaskToCompatibleEmployeesToTaskIds() {
        return convertTaskToCompatibleEmployeesToTaskIds(taskToCompatibleEmployeesWorkShifts);
    }

    public List<EmployeeWorkShift> getEmployeeWorkShiftList() {
        return employeeWorkShiftList;
    }

    public Map<EmployeeWorkShift, Integer> getEmployeeIdsToIndex() {
        return employeeIdsToIndex;
    }

    public TransportInformation getTransportInformation() {
        return transportInformation;
    }

    public ErrorAppender getErrorAppender() {
        return errorAppender;
    }

    public List<Task> getSyncedTasks() {
        return syncedTasks;
    }

    public List<Task> getSingleTasks() {
        return singleTasks;
    }

    public List<Task> getSyncedTasksTimeChangeable() {
        return syncedTasksTimeChangeable;
    }

    public List<Task> getSyncedTasksEmployeeChangeable() {
        return syncedTasksEmployeeChangeable;
    }

    public Map<String, Set<InfeasibilityType>> getOriginalTaskIdToInfeasibilityTypes() {
        return originalTaskIdToInfeasibilityTypes;
    }

    public HeavyTaskChecker getHeavyTaskChecker() {
        return heavyTaskChecker;
    }

    public NonAllocatedFeasibilityCheck getNonAllocatedFeasibilityCheck() {
        return nonAllocatedFeasibilityCheck;
    }

    public int getNumberOfStrictTasks() {
        return strictTasks.size();
    }

    public void removeTask(Task task) {
        if (singleTasks != null) {
            singleTasks.remove(task);
        }
        if (syncedTasks != null) {
            syncedTasks.remove(task);
            syncedTasksTimeChangeable.remove(task);
            syncedTasksEmployeeChangeable.remove(task);
        }
        if (strictTasks != null) {
            strictTasks.remove(task);
        }
    }

    public VisitHistoryFitnessCreator getVisitHistoryFitnessCreator() {
        return visitHistoryFitnessCreator;
    }

    public EmployeeWorkShift getInitialEmployeeAssignmentFor(Task task) {
        return taskToInitialEmployeeWorkShiftAssignment.get(task);
    }

    public void setInitialEmployeeAssignmentFor(Task task, EmployeeWorkShift employeeWorkShift) {
        taskToInitialEmployeeWorkShiftAssignment.put(task, employeeWorkShift);
    }
}
