package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval.task;

import aws.constants.ApiConstants;
import aws.errors.ErrorResponseSaver;
import aws.errors.detail.internal.NotPossibleToDecreaseNumberOfEmployeeForGeneralTasksError;
import aws.errors.detail.internal.ShiftCodeValidationError;
import aws.errors.parent.InternalProgramError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import feasibilitychecker.taskfeasibility.entities.infeasibilitytype.InfeasibilityType;
import inputhandler.entityhelpers.EmployeeTaskHelper;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.generaltasks.EmployeeTaskType;
import probleminstance.entities.restrictions.VisitRestriction;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.initializers.restrictions.TaskVisitRestrictionsCreator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralTask extends Task {


    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_NUMBER_OF_EMPLOYEES)
    private Integer numberOfEmployees;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASK_IS_SYNCHRONIZED)
    private Boolean isGeneralTaskSynchronized;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASK_ALLOWED_SHIFT_CODES)
    private List<String> allowedShiftCodes;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASK_PROHIBITED_SHIFT_CODES)
    private List<String> prohibitedShiftCodes;

    @JsonIgnore
    private Set<EmployeeWorkShift> compatibleEmployees;


    @JsonCreator
    public GeneralTask(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_DATE, required = true) String fromDate,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_DATE, required = true) String toDate,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_TIME, required = true) String fromTime,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_TIME, required = true) String toTime,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_ID, required = true) String taskId,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_DOES_REQUIRE_PHYSICAL_APPEARANCE) Boolean requirePhysicalAppearance,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_DOES_NEED_TO_BE_PLANNED_FOR) Boolean needToBePlannedFor,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_STRICT_TIME_WINDOWS) Boolean strictTimeWindows,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_SKILL_REQUIREMENT) Integer requiredSkillLevel,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_NUMBER_OF_HIGHER_SKILL_LEVELS_ACCEPTED) Integer numberOfHigherSkillLevelsAccepted,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_SERVICE_NAME) String serviceName,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_ORIGINAL_TASK_ID) String originalTaskId,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_NUMBER_OF_EMPLOYEES) Integer numberOfEmployees,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASK_IS_SYNCHRONIZED) Boolean isGeneralTaskSynchronized,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_IS_HEAVY) Boolean isHeavy,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASK_ALLOWED_SHIFT_CODES) List<String> allowedShiftCodes,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASK_PROHIBITED_SHIFT_CODES) List<String> prohibitedShiftCodes,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE) String shiftType,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_SYNCED_INITIALLY) Boolean isSyncedInitially) {
        super(durationMinutes, fromDate, toDate, fromTime, toTime, taskId, requirePhysicalAppearance, needToBePlannedFor,
                strictTimeWindows, requiredSkillLevel, numberOfHigherSkillLevelsAccepted, serviceName, originalTaskId, isHeavy, shiftType, isSyncedInitially);
        this.numberOfEmployees = numberOfEmployees;
        this.isGeneralTaskSynchronized = isGeneralTaskSynchronized;
        this.compatibleEmployees = new HashSet<>();
        this.allowedShiftCodes = allowedShiftCodes;
        this.prohibitedShiftCodes = prohibitedShiftCodes;
    }

    @Override
    protected void processRawDataSub(Configuration configuration, ProblemInstance problemInstance) {
        compatibleEmployees = new HashSet<>();
        initialize(problemInstance);

    }

    private void initialize(ProblemInstance problemInstance) {
        super.setAddressEntity(problemInstance.getOfficeInfo());
        if (getNumberOfHigherSkillLevelsAccepted() < 0 || getRequiredSkillLevel() < 0) {
            setNumberOfHigherSkillLevelsAccepted(1000);
        }
        updateCompatibleEmployees(problemInstance);
        if (taskIsSyncedAndNotEnoughEmployees()) {
            setWarningAndAddVisitRestrictionsForExcessTasks(problemInstance, InfeasibilityType.SYNCED_CAPACITY);
        } else if (shouldAddTaskToAllCompatibleEmployees()) {
            addTaskToAllCompatibleEmployeeWorkShifts(problemInstance, compatibleEmployees);
            if (numberOfEmployees != null && numberOfEmployees > compatibleEmployees.size()) {
                setWarningAndAddVisitRestrictionsForExcessTasks(problemInstance, InfeasibilityType.GENERAL_TASK_CAPACITY);
            }
            if (numberOfEmployees != null && numberOfEmployees == -1 && compatibleEmployees.isEmpty()) {
                numberOfEmployees = 1;
                createEmployeeTasksFromGeneralTask(problemInstance);
            }
        } else {
            createEmployeeTasksFromGeneralTask(problemInstance);
        }
    }

    private boolean taskIsSyncedAndNotEnoughEmployees(){
        return isGeneralTaskSynchronized != null && isGeneralTaskSynchronized && numberOfEmployees > compatibleEmployees.size();
    }

    public boolean shouldAddTaskToAllCompatibleEmployees() {
        return numberOfEmployees == null || numberOfEmployees == -1 || (numberOfEmployees != 0 && numberOfEmployees >= compatibleEmployees.size());
    }

    private void addTaskToAllCompatibleEmployeeWorkShifts(ProblemInstance problemInstance, Collection<EmployeeWorkShift> compatibleEmployees) {
        for (EmployeeWorkShift employeeWorkShift : compatibleEmployees) {
            EmployeeTaskHelper.addEmployeeTaskFromGeneralTaskForSpecificEmployee(this, problemInstance, employeeWorkShift);
        }
    }

    private void setWarningAndAddVisitRestrictionsForExcessTasks(ProblemInstance problemInstance, InfeasibilityType infeasibilityType) {
        for (int i = EmployeeTaskType.START_VALUE_FOR_UNSPECIFIED_GE_TASK;
             i < numberOfEmployees - compatibleEmployees.size() + EmployeeTaskType.START_VALUE_FOR_UNSPECIFIED_GE_TASK; i++) {
            Task task = EmployeeTaskHelper.addEmployeeTaskUnspecifiedEmployee(this, problemInstance, i);
            VisitRestriction.addVisitRestrictionsToAllEmployees(task, problemInstance, infeasibilityType);
        }
    }

    private void createEmployeeTasksFromGeneralTask(ProblemInstance problemInstance) {
        for (int i = EmployeeTaskType.START_VALUE_FOR_UNSPECIFIED_GE_TASK;
             i < numberOfEmployees + EmployeeTaskType.START_VALUE_FOR_UNSPECIFIED_GE_TASK; i++) {
            EmployeeTaskHelper.addEmployeeTaskUnspecifiedEmployee(this, problemInstance, i);
        }
    }

    private void updateCompatibleEmployees(ProblemInstance problemInstance) {
        compatibleEmployees = new HashSet<>();
        for (EmployeeWorkShift employeeWorkShift : problemInstance.getEmployeeStructure().getAllEmployeeWorkShifts()) {
            if (TaskVisitRestrictionsCreator.employeeCanDoTaskBasedOnShiftAndSkill(
                    employeeWorkShift,
                    this)
                    && employeeWorkShift.getIsActive()
                    && !employeeAlreadyHasEmployeeTask(problemInstance, employeeWorkShift)
                    && employeeAllowedBasedOnWorkShiftCodes(problemInstance, employeeWorkShift)) {
                compatibleEmployees.add(employeeWorkShift);
            }
        }
    }

    private boolean employeeAllowedBasedOnWorkShiftCodes(ProblemInstance problemInstance, EmployeeWorkShift employeeWorkShift) {
        if (employeeWorkShift.getShiftCode() == null) {
            return true;
        }
        if (allowedShiftCodes != null && !allowedShiftCodes.isEmpty()) {
            return employeeShiftCodeAllowed(employeeWorkShift);
        }
        if (prohibitedShiftCodes != null && !prohibitedShiftCodes.isEmpty()) {
            return employeeShiftCodeNotInProhibited(employeeWorkShift);
        }
        if (allowedShiftCodes != null && prohibitedShiftCodes != null || allowedShiftCodes == null && prohibitedShiftCodes == null){
            return true;
        }
        ErrorResponseSaver.saveError(new InternalProgramError(), new ShiftCodeValidationError(), problemInstance.getErrorAppender(), new Exception());
        return false;
    }

    private boolean employeeShiftCodeAllowed(EmployeeWorkShift employeeWorkShift) {
        return allowedShiftCodes.contains(employeeWorkShift.getShiftCode());
    }

    private boolean employeeShiftCodeNotInProhibited(EmployeeWorkShift employeeWorkShift) {
        return !prohibitedShiftCodes.contains(employeeWorkShift.getShiftCode());
    }

    private boolean employeeAlreadyHasEmployeeTask(ProblemInstance problemInstance, EmployeeWorkShift employeeWorkShift) {
        Set<EmployeeWorkShift> employeeIdsForOriginalTaskId = problemInstance.getProblemInstanceTaskStructure().getEmployeeWorkShiftsForOriginalEmployeeTaskId(getOriginalTaskId());
        return employeeIdsForOriginalTaskId != null && employeeIdsForOriginalTaskId.contains(employeeWorkShift);
    }

    @JsonIgnore
    public int getNumberOfUnAssignedTasks() {
        if (numberOfEmployees == null || numberOfEmployees == -1) {
            return 0;
        }
        return numberOfEmployees - compatibleEmployees.size();
    }

    public void decreaseNumberOfEmployees(ProblemInstance problemInstance) {
        if (numberOfEmployees > 0) {
            setNumberOfEmployees(numberOfEmployees - 1);
        } else {
            ErrorResponseSaver.saveError(new InternalProgramError(),
                    new NotPossibleToDecreaseNumberOfEmployeeForGeneralTasksError(getTaskId()), problemInstance.getErrorAppender(), new Exception());
        }
    }

    public void increaseNumberOfEmployees() {
        setNumberOfEmployees(numberOfEmployees + 1);
    }

    public List<String> getAllowedShiftCodes() {
        return allowedShiftCodes;
    }

    public List<String> getProhibitedShiftCodes() {
        return prohibitedShiftCodes;
    }

    @JsonIgnore
    public Boolean isGeneralTaskSynchronized() {
        return isGeneralTaskSynchronized;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(int numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public Set<EmployeeWorkShift> getCompatibleEmployees() {
        return compatibleEmployees;
    }

}
