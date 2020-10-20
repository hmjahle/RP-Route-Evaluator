package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.employee.Employee;
import probleminstance.entities.transport.Transport;

import javax.validation.constraints.NotNull;

public class EmployeeWorkShift extends WorkShift {

    /**
     * This class merges employee and workshift. An employee may have multiple work shifts.
     */

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_NAME)
    private String employeeName;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_DEPARTMENT)
    private String department;

    @NotNull
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SKILL_LEVEL)
    private Integer skillLevelId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TRANSPORT)
    private Transport transport;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ACTIVE)
    private Boolean isActive;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SHIFT_CODE)
    private String shiftCode;


    public EmployeeWorkShift(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_DATE, required = true) String fromDate,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_DATE, required = true) String toDate,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_TIME, required = true) String fromTime,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_TIME, required = true) String toTime,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKSHIFT_ID) String workShiftId,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID, required = true) String employeeId,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ORIGINAL_ID) String originalEmployeeId,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPE) String shiftType,
                             @NotNull @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SKILL_LEVEL) Integer skillLevelId,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TRANSPORT) Transport transport,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ACTIVE) Boolean isActive,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SHIFT_CODE) String shiftCode,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_NAME) String employeeName,
                             @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_DEPARTMENT) String department) {
        super(durationMinutes, fromDate, toDate, fromTime, toTime, workShiftId, employeeId, originalEmployeeId, shiftType);
        this.skillLevelId = skillLevelId;
        this.transport = transport;
        this.isActive = isActive;
        this.shiftCode = shiftCode;
        this.employeeName = employeeName;
        this.department = department;
    }

    public EmployeeWorkShift(Employee employee, WorkShift workShift, ProblemInstance problemInstance){
        this(workShift.getDurationMinutes(), workShift.getFromDate(), workShift.toDate, workShift.fromTime, workShift.toTime,
                workShift.getWorkShiftId(), employee.getEmployeeId(), employee.getOriginalEmployeeId(), workShift.getShiftType(),
                employee.getSkillLevelId(), employee.getTransport(), employee.getIsActive(), employee.getShiftCode(), employee.getEmployeeName(), employee.getDepartment());
        if(problemInstance != null){
            processRawData(problemInstance.getConfiguration(), problemInstance);
        }
    }


    public Integer getSkillLevelId() {
        return skillLevelId;
    }

    public void setSkillLevelId(Integer skillLevelId) {
        this.skillLevelId = skillLevelId;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
        if (transport == null) {
            transport = problemInstance.getOfficeInfo().getDefaultTransportMode();
        }
        if (isActive == null) {
            isActive = true;
        }
        super.processRawData(configuration, problemInstance);
    }

    @Override
    public String toString() {
        return "EmployeeWorkShift{" +
                "employeeId=" + getEmployeeId() +
                "workShiftId=" + getWorkShiftId() +
                "startTime=" + getDateHandler().getTimeStringFromSeconds(getStartTime(), "MM-dd HH:mm") +
                ", endTime=" + getDateHandler().getTimeStringFromSeconds(getEndTime(), "MM-dd HH:mm") +
                '}';
    }


}
