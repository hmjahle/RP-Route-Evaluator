package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval.task;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import feasibilitychecker.taskfeasibility.entities.infeasibilitytype.InfeasibilityType;
import inputhandler.entityhelpers.EmployeeTaskHelper;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.generaltasks.EmployeeTaskType;
import probleminstance.entities.restrictions.TaskToRequiredEmployees;
import probleminstance.entities.restrictions.VisitRestriction;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.initializers.restrictions.TaskVisitRestrictionsCreator;

public class EmployeeTask extends Task {


    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID)
    private String originalEmployeeId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKSHIFT_ID)
    private String workShiftId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASK_FIXED_TO_EMPLOYEE)
    private Boolean fixedToEmployee;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASK_TYPE)
    private EmployeeTaskType employeeTaskType;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASK_IS_SYNCHRONIZED)
    private Boolean isEmployeeTaskSynchronized;

    @JsonCreator
    public EmployeeTask(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
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
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID, required = true) String originalEmployeeId,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASK_FIXED_TO_EMPLOYEE) Boolean fixedToEmployee,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASK_TYPE) EmployeeTaskType employeeTaskType,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASK_IS_SYNCHRONIZED) Boolean isEmployeeTaskSynchronized,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_IS_HEAVY) Boolean isHeavy,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE) String shiftType,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKSHIFT_ID) String workShiftId,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_SYNCED_INITIALLY) Boolean isSyncedInitially) {
        super(durationMinutes, fromDate, toDate, fromTime, toTime, taskId, requirePhysicalAppearance, needToBePlannedFor,
                strictTimeWindows, requiredSkillLevel, numberOfHigherSkillLevelsAccepted, serviceName, originalTaskId, isHeavy, shiftType, isSyncedInitially);
        this.originalEmployeeId = originalEmployeeId;
        this.fixedToEmployee = fixedToEmployee == null ? Boolean.TRUE : fixedToEmployee;
        this.workShiftId = workShiftId;
        this.employeeTaskType = employeeTaskType;
        this.isEmployeeTaskSynchronized = isEmployeeTaskSynchronized == null ? Boolean.FALSE : isEmployeeTaskSynchronized;
        if (this.employeeTaskType == null) {
            this.employeeTaskType = EmployeeTaskType.ORG_EMP_TASK;
        }
    }

    private void allocateEmployeeTaskToCorrectEmployee(ProblemInstance problemInstance) {
        if(problemInstance.getEmployeeStructure().getEmployeeWorkShiftsForOriginalEmployeeId(originalEmployeeId) != null){
            for (EmployeeWorkShift employeeWorkShift : problemInstance.getEmployeeStructure().getEmployeeWorkShiftsForOriginalEmployeeId(originalEmployeeId)) {
                boolean employeeCanDoTask = TaskVisitRestrictionsCreator.employeeCanDoTaskBasedOnShiftAndSkill(
                        employeeWorkShift, this);
                if (employeeCanDoTask && employeeWorkShift.getIsActive()) {
                    allocateTaskToEmployee(problemInstance, employeeWorkShift);
                    return;
                }
            }
        }
        //No employee was allocated
        VisitRestriction.addVisitRestrictionsToAllEmployees(this, problemInstance, InfeasibilityType.WORK_SHIFT_OR_SKILL_LEVEL);
    }

    private void allocateTaskToEmployee(ProblemInstance problemInstance, EmployeeWorkShift employeeWorkShift) {
        workShiftId = employeeWorkShift.getWorkShiftId();
        problemInstance.getProblemInstanceTaskStructure().assignEmployeeTaskToEmployee(employeeWorkShift, this);
        VisitRestriction.addVisitRestrictionsFrom(this, new TaskToRequiredEmployees(getTaskId(), employeeWorkShift.getOriginalEmployeeId()), problemInstance, null);
    }

    @Override
    public String toString() {
        return "EmployeeTask{id=" + this.getTaskId() + ", startTime=" + getDateHandler().getTimeStringFromSeconds(getStartTime(), "HH:mm")
                + ", endTime=" + getDateHandler().getTimeStringFromSeconds(getEndTime(), "HH:mm") + "}";
    }

    @Override
    protected void processRawDataSub(Configuration configuration, ProblemInstance problemInstance) {
        super.setAddressEntity(problemInstance.getOfficeInfo());
        EmployeeTaskHelper.validateWorkShiftIdInEmployeeTask(this, problemInstance);
        if (problemInstance.getEmployeeStructure().getOriginalEmployeeIds().contains(originalEmployeeId)) {
            allocateEmployeeTaskToCorrectEmployee(problemInstance);
        }
    }

    @JsonIgnore
    public boolean isEmployeeTaskAssignedToAnEmployee() {
        return originalEmployeeId != null;
    }

    public void setFixedToEmployee(Boolean fixedToEmployee) {
        this.fixedToEmployee = fixedToEmployee;
    }

    public EmployeeTaskType getEmployeeTaskType() {
        return employeeTaskType;
    }

    public String getOriginalEmployeeId() {
        return originalEmployeeId;
    }

    public String getWorkShiftId() {
        return workShiftId;
    }

    @JsonIgnore
    public Boolean isEmployeeTaskSynchronized() {
        return isEmployeeTaskSynchronized;
    }
}


