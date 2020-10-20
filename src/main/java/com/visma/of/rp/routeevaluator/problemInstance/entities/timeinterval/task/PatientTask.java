package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval.task;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import inputhandler.entityhelpers.TaskHelper;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.address.Patient;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class PatientTask extends Task {


    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_ID)
    private String patientId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_NUMBER_OF_EMPLOYEES)
    @Min(1)
    private Integer numberOfEmployees;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_SERVICE_ID)
    private String serviceId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_DESCRIPTION)
    private String taskDescription;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_PERMANENT)
    private Boolean taskIsPermanent;

    @JsonCreator
    public PatientTask(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
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
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_ID) @NotEmpty String patientId,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_NUMBER_OF_EMPLOYEES) @Min(1) Integer numberOfEmployees,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_SERVICE_ID) String serviceId,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_DESCRIPTION) String taskDescription,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE) String shiftType,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_PERMANENT) Boolean taskIsPermanent,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_IS_HEAVY) Boolean isHeavy,
                       @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_SYNCED_INITIALLY) Boolean isSyncedInitially) {
        super(durationMinutes, fromDate, toDate, fromTime, toTime, taskId, requirePhysicalAppearance, needToBePlannedFor,
                strictTimeWindows, requiredSkillLevel, numberOfHigherSkillLevelsAccepted, serviceName, originalTaskId, isHeavy, shiftType, isSyncedInitially);
        this.patientId = patientId;
        this.numberOfEmployees = numberOfEmployees;
        this.serviceId = serviceId;
        this.taskDescription = taskDescription;
        this.taskIsPermanent = taskIsPermanent;
    }

    public String getPatientId() {
        return patientId;
    }

    @JsonIgnore
    public Patient getPatient() {
        return (Patient) super.getAddressEntity();
    }

    public void setNumberOfEmployees(int numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    @Override
    public String toString() {
        return "PatientTask{" +
                "id=" + getTaskId() +
                ", requiredSkillLevel=" + super.getRequiredSkillLevel() +
                ", startTime=" + getDateHandler().getTimeStringFromSeconds(getStartTime(), "yyyy-MM-dd HH:mm:ss") +
                ", endTime=" + getDateHandler().getTimeStringFromSeconds(getEndTime(), "yyyy-MM-dd HH:mm:ss") +
                ", patient=" + getPatientId() +
                '}';
    }


    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    @Override
    protected void processRawDataSub(Configuration configuration, ProblemInstance problemInstance) {
        if (numberOfEmployees == null) {
            numberOfEmployees = 1;
        }
        super.setAddressEntity(problemInstance.getPatients().get(patientId));
        if (taskIsPermanent == null) {
            setTaskIsPermanent(true);
        }
        adjustDuration(problemInstance);
    }

    private void adjustDuration(ProblemInstance problemInstance) {
        double multiplier = 1 + problemInstance.getOfficeInfo().getPatientTaskDurationAdjustmentPercentage();
        setDurationSeconds((long) (Math.floor(getDurationSeconds() * multiplier)));
        TaskHelper.updateTimeWindowBasedOnNewDuration(this);
    }


    public Boolean getTaskIsPermanent() {
        return taskIsPermanent;
    }

    public void setTaskIsPermanent(Boolean taskIsPermanent) {
        this.taskIsPermanent = taskIsPermanent;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getServiceId() {
        return serviceId;
    }
}
