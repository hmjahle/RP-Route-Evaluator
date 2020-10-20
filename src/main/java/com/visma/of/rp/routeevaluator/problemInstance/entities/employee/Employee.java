package com.visma.of.rp.routeevaluator.problemInstance.entities.employee;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.interfaces.EntityInterface;
import probleminstance.entities.transport.Transport;

import javax.validation.constraints.NotNull;

public class Employee extends EmployeeBasics implements EntityInterface {

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_NAME)
    private String employeeName;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_DEPARTMENT)
    private String department;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SHIFT_CODE)
    private String shiftCode;
    @JsonIgnore
    private String originalEmployeeId;
    @JsonIgnore
    private String workHourId;

    @JsonCreator
    public Employee(@NotNull @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID, required = true) String employeeId,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ORIGINAL_ID) String originalEmployeeId,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_NAME) String employeeName,
                    @NotNull @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SKILL_LEVEL) Integer skillLevelId,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TRANSPORT) Transport transport,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_DEPARTMENT) String department,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ACTIVE) Boolean isActive,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SHIFT_CODE) String shiftCode,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKSHIFT_ID) String workHourId){
        super(employeeId, skillLevelId, transport, isActive);
        this.originalEmployeeId = originalEmployeeId == null ? employeeId : originalEmployeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.shiftCode = shiftCode;
        this.workHourId = workHourId;
    }


    public String getDepartment() {
        return department;
    }

    public String getOriginalEmployeeId() {
        return originalEmployeeId;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public String getWorkHourId() {
        return workHourId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", originalEmployeeId='" + originalEmployeeId + '\'' +
                '}';
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return super.getUniqueId();
    }

    @Override
    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
        super.processRawData(configuration, problemInstance);
    }
}
