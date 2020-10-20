package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval.task;

import aws.constants.ApiConstants;
import aws.entity.ErrorAppender;
import aws.errors.ErrorResponseSaver;
import aws.errors.detail.validation.DurationExceedsTimeWindowError;
import aws.errors.parent.DataValidationError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import inputhandler.entityhelpers.TaskHelper;
import probleminstance.ProblemInstance;
import probleminstance.ProblemInstanceHelpStructure;
import probleminstance.entities.Configuration;
import probleminstance.entities.SyncedTaskPair;
import probleminstance.entities.address.AddressEntity;
import probleminstance.entities.interfaces.EntityInterface;
import probleminstance.entities.interfaces.ShiftTypeEntity;
import probleminstance.entities.timeinterval.TimeInterval;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class Task extends TimeInterval implements EntityInterface, ShiftTypeEntity {

    @JsonIgnore
    public static final Integer DEFAULT_SKILL_LEVEL = -1;

    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_ID)
    private String taskId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_DOES_REQUIRE_PHYSICAL_APPEARANCE)
    private Boolean requirePhysicalAppearance;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_DOES_NEED_TO_BE_PLANNED_FOR)
    private Boolean needToBePlannedFor;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_STRICT_TIME_WINDOWS)
    private Boolean strictTimeWindows;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_SKILL_REQUIREMENT)
    private Integer requiredSkillLevel;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_NUMBER_OF_HIGHER_SKILL_LEVELS_ACCEPTED)
    @Min(0)
    private Integer numberOfHigherSkillLevelsAccepted;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_SERVICE_NAME)
    private String serviceName;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_ORIGINAL_TASK_ID)
    private String originalTaskId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_IS_HEAVY)
    private Boolean isHeavy;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE)
    private String shiftType;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_SYNCED_INITIALLY)
    private Boolean syncedInitially;

    @JsonIgnore
    private AddressEntity addressEntity;
    @JsonIgnore
    private Set<Task> syncedTasksExcludingItself;
    @JsonIgnore
    private Set<Task> syncedTasksIncludingItself;
    @JsonIgnore
    private Set<Task> listIncompatibleTasks;
    @JsonIgnore
    private long lowToHighThreshold;
    @JsonIgnore
    private int hashKey;

    @JsonIgnore
    private long syncedWithStartIntervalSeconds = 0;
    @JsonIgnore
    private long syncedWithEndIntervalSeconds = 0;
    @JsonIgnore
    private long syncedWithIntervalDiffSeconds = 0;
    @JsonIgnore
    private boolean isDependent = false;

    @JsonCreator
    public Task(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
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
                @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_IS_HEAVY) Boolean isHeavy,
                @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE) String shiftType,
                @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_TASK_IS_SYNCED_INITIALLY) Boolean syncedInitially) {
        super(durationMinutes, fromDate, toDate, fromTime, toTime);
        this.taskId = taskId;
        this.requirePhysicalAppearance = requirePhysicalAppearance;
        this.needToBePlannedFor = needToBePlannedFor;
        this.strictTimeWindows = strictTimeWindows;
        this.requiredSkillLevel = requiredSkillLevel;
        this.numberOfHigherSkillLevelsAccepted = numberOfHigherSkillLevelsAccepted;
        this.serviceName = serviceName;
        this.originalTaskId = originalTaskId;
        this.isHeavy = isHeavy;
        this.syncedTasksExcludingItself = new HashSet<>();
        this.syncedTasksIncludingItself = new HashSet<>();
        this.listIncompatibleTasks = new HashSet<>();
        this.shiftType = shiftType;
        this.hashKey = taskId == null ? 0 : taskId.hashCode();
        this.syncedInitially = syncedInitially;
    }

    public String getTaskId() {
        return taskId;
    }

    public Integer getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    public AddressEntity getAddressEntity() {
        return addressEntity;
    }

    public void setAddressEntity(AddressEntity addressEntity) {
        this.addressEntity = addressEntity;
    }

    public void setRequiredSkillLevel(int requiredSkillLevel) {
        this.requiredSkillLevel = requiredSkillLevel;
    }

    private void setLowToHighTimeWindowThreshold(Configuration configuration) {
        long slack = getEndTime() - getStartTime() - getDurationSeconds();
        double slackThreshold = configuration.getTimeWindowHighPatientTaskConsiderationThreshold();
        this.lowToHighThreshold = (slack < slackThreshold) ? 0 : configuration.getTimeWindowLowToHighThresholdSeconds();
    }

    public String getOriginalTaskId() {
        return originalTaskId;
    }

    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
        super.processRawData(problemInstance);
        if (requirePhysicalAppearance == null) {
            requirePhysicalAppearance = true;
        }
        if (needToBePlannedFor == null) {
            needToBePlannedFor = true;
        }
        if (strictTimeWindows == null) {
            strictTimeWindows = false;
        }
        if (numberOfHigherSkillLevelsAccepted == null) {
            numberOfHigherSkillLevelsAccepted = 1000;
        }
        if (requiredSkillLevel == null) {
            requiredSkillLevel = DEFAULT_SKILL_LEVEL;
        }
        if (isHeavy == null) {
            isHeavy = false;
        }
        syncedTasksExcludingItself = new HashSet<>();
        syncedTasksIncludingItself = new HashSet<>();
        listIncompatibleTasks = new HashSet<>();
        if (originalTaskId == null) {
            originalTaskId = taskId + "";
        }
        verifyDurationAndTimeWindows(getOriginalTaskId(), getStartTime(), getEndTime(), getDurationSeconds(), problemInstance.getErrorAppender());
        setLowToHighTimeWindowThreshold(configuration);
        processRawDataSub(configuration, problemInstance);
        TaskHelper.updateTimeWindowBasedOnTaskSlackOnlyOnPatientTasks(this, problemInstance);
    }

    public static boolean verifyDurationAndTimeWindows(String taskId, Long startTime, Long endTime, Long duration, ErrorAppender errorAppender) {
        if (endTime - startTime < duration) {
            ErrorResponseSaver.saveError(new DataValidationError(), new DurationExceedsTimeWindowError(taskId), errorAppender, new Exception());
            return false;
        }
        return true;
    }

    protected abstract void processRawDataSub(Configuration configuration, ProblemInstance problemInstance);

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return taskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + taskId +
                ", startTime=" + getDateHandler().getTimeStringFromSeconds(getStartTime()) +
                ", endTime=" + getDateHandler().getTimeStringFromSeconds(getEndTime()) +
                '}';
    }

    public Boolean getRequirePhysicalAppearance() {
        return requirePhysicalAppearance;
    }

    public Boolean getNeedToBePlannedFor() {
        return needToBePlannedFor;
    }

    @JsonIgnore
    public Boolean isStrict() {
        return strictTimeWindows;
    }

    public void addSyncedTasks(Collection<Task> syncedTasksExcludingItself) {
        this.syncedTasksExcludingItself.addAll(syncedTasksExcludingItself);
        syncedTasksIncludingItself.addAll(syncedTasksExcludingItself);
        syncedTasksIncludingItself.add(this);
        addListIncompatibleTasks(syncedTasksExcludingItself);
        syncedInitially = true;
    }


    public Boolean getSyncedInitially() {
        return syncedInitially;
    }

    public void addListIncompatibleTasks(Collection<Task> listIncompatibleTasks) {
        this.listIncompatibleTasks.addAll(listIncompatibleTasks);
    }

    public Set<Task> getSyncedTasksExcludingItself() {
        return syncedTasksExcludingItself;
    }

    public Set<Task> getSyncedTasksIncludingItself() {
        return syncedTasksIncludingItself;
    }

    public Set<Task> getListIncompatibleTasks() {
        return listIncompatibleTasks;
    }

    @JsonIgnore
    public boolean isSynced() {
        return !syncedTasksExcludingItself.isEmpty();
    }


    public void setStrictTimeWindows(boolean strictTimeWindows) {
        this.strictTimeWindows = strictTimeWindows;
    }

    public long getLowToHighThreshold() {
        return lowToHighThreshold;
    }

    public Integer getNumberOfHigherSkillLevelsAccepted() {
        return numberOfHigherSkillLevelsAccepted;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setNumberOfHigherSkillLevelsAccepted(int numberOfHigherSkillLevelsAccepted) {
        this.numberOfHigherSkillLevelsAccepted = numberOfHigherSkillLevelsAccepted;
    }

    public void makeSingle(ProblemInstance problemInstance) {
        if (isSynced()) {
            Collection<Task> otherSyncedTasks = new ArrayList<>(syncedTasksExcludingItself);
            removeAllTaskConnections();
            removeSyncedFromHelpStructure(this, problemInstance);
            for (Task otherTaskId : otherSyncedTasks) {
                updateOtherSyncTask(problemInstance, otherTaskId);
            }
        }
    }

    private void updateOtherSyncTask(ProblemInstance problemInstance, Task otherTask) {
        otherTask.syncedTasksExcludingItself.remove(this);
        otherTask.syncedTasksIncludingItself.remove(this);
        otherTask.listIncompatibleTasks.remove(this);
        if (otherTask.syncedTasksExcludingItself.isEmpty()) {
            otherTask.removeAllTaskConnections();
            removeSyncedFromHelpStructure(otherTask, problemInstance);
        }
    }

    private void removeAllTaskConnections() {
        strictTimeWindows = false;
        syncedTasksExcludingItself = new HashSet<>();
        syncedTasksIncludingItself = new HashSet<>();
        listIncompatibleTasks = new HashSet<>();
    }

    private void removeSyncedFromHelpStructure(Task task, ProblemInstance problemInstance) {
        ProblemInstanceHelpStructure helpStructure = problemInstance.getProblemInstanceHelpStructure();
        if (helpStructure.getSyncedTasks() != null && helpStructure.getSyncedTasks().contains(task)) {
            helpStructure.getSyncedTasks().remove(task);
            helpStructure.getSyncedTasksEmployeeChangeable().remove(task);
            helpStructure.getSyncedTasksTimeChangeable().remove(task);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return hashKey;
    }

    @JsonIgnore
    public Boolean isHeavy() {
        return isHeavy;
    }

    public String getShiftType() {
        return shiftType;
    }


    public long getSyncedWithStartIntervalSeconds() {
        return syncedWithStartIntervalSeconds;
    }

    public long getSyncedWithEndIntervalSeconds() {
        return syncedWithEndIntervalSeconds;
    }

    public void makeDependentOn(SyncedTaskPair pair, Task masterTask) {
        this.syncedWithStartIntervalSeconds = pair.getSyncedWithIntervalStartSeconds();
        this.syncedWithEndIntervalSeconds = pair.getSyncedWithIntervalEndSeconds();
        this.syncedWithIntervalDiffSeconds = syncedWithEndIntervalSeconds - syncedWithStartIntervalSeconds;
        this.startTime = masterTask.getStartTime();
        this.endTime = masterTask.getEndTime() + syncedWithEndIntervalSeconds + durationSeconds;
        isDependent = true;
    }

    public long getSyncedWithIntervalDiffSeconds() {
        return syncedWithIntervalDiffSeconds;
    }


    // Temporal dependency
    @JsonIgnore
    public boolean isDependent() {
        return isDependent;
    }
}


