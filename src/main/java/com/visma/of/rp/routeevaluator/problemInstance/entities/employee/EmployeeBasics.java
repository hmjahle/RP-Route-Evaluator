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

public class EmployeeBasics implements EntityInterface {

    @NotNull
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID, required = true)
    private String employeeId;

    @NotNull
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SKILL_LEVEL)
    private Integer skillLevelId;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TRANSPORT)
    private Transport transport;

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ACTIVE)
    private Boolean isActive;

    @JsonCreator
    public EmployeeBasics(@NotNull @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ID, required = true) String employeeId,
                    @NotNull @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_SKILL_LEVEL) Integer skillLevelId,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TRANSPORT) Transport transport,
                    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_ACTIVE) Boolean isActive){
        this.employeeId = employeeId;
        this.skillLevelId = skillLevelId;
        this.transport = transport;
        this.isActive = isActive;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "EmployeeBasics{" +
                "employeeId='" + employeeId + '\'' +
                ", skillLevelId=" + skillLevelId +
                ", transport=" + transport +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return employeeId;
    }

    @Override
    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
        if (transport == null) {
            transport = problemInstance.getOfficeInfo().getDefaultTransportMode();
        }
        if(isActive == null){
            isActive = true;
        }
    }
}
