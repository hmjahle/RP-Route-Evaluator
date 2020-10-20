package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.interfaces.EntityInterface;
import probleminstance.entities.interfaces.ShiftTypeEntity;

public class WorkShift extends TimeInterval implements EntityInterface, ShiftTypeEntity {

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKSHIFT_ID)
    private String workShiftId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID)
    private String employeeId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ORIGINAL_ID)
    private String originalEmployeeId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE)
    private String shiftType;

    @JsonIgnore
    private String uniqueId;

    public WorkShift(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_DATE, required = true) String fromDate,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_DATE, required = true) String toDate,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_TIME, required = true) String fromTime,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_TIME, required = true) String toTime,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKSHIFT_ID) String workShiftId,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID, required = true) String employeeId,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ORIGINAL_ID) String originalEmployeeId,
                     @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE) String shiftType) {
        super(durationMinutes, fromDate, toDate, fromTime, toTime);
        this.workShiftId = workShiftId;
        this.originalEmployeeId = originalEmployeeId;
        this.employeeId = employeeId;
        this.shiftType = shiftType;
    }

    public String getWorkShiftId() {
        return workShiftId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getOriginalEmployeeId() {
        return originalEmployeeId;
    }

    @Override
    public String toString() {
        return "WorkShift{" +
                "startTime=" + getDateHandler().getTimeStringFromSeconds(getStartTime(), "yyyy-MM-dd HH:mm:ss") +
                ", endTime=" + getDateHandler().getTimeStringFromSeconds(getEndTime(), "yyyy-MM-dd HH:mm:ss") +
                '}';
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
        super.processRawData(problemInstance);
        if (workShiftId == null) {
            workShiftId = employeeId;
            uniqueId = employeeId + ":" + fromDate + "-" + fromTime + " - " + toDate + "-" + toTime;
        } else {
            uniqueId = workShiftId;
        }
        if (originalEmployeeId == null) {
            originalEmployeeId = employeeId;
        }
    }

    public void addShiftTimeToWorkShiftId(){
        workShiftId = employeeId + ":" + fromDate + "-" + fromTime + " - " + toDate + "-" + toTime;
    }

    public String getShiftType() {
        return shiftType;
    }
}
