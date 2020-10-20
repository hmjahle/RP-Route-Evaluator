package com.visma.of.rp.routeevaluator.problemInstance.entities.generaltasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import probleminstance.entities.timeinterval.EmployeeWorkShift;

public enum EmployeeTaskType {

    @JsonProperty("ORG_EMP_TASK")
    ORG_EMP_TASK(""),
    @JsonProperty("UNSPECIFIED_GE_TASK")
    UNSPECIFIED_GE_TASK("-num-"),
    @JsonProperty("SPECIFIED_GE_TASK")
    SPECIFIED_GE_TASK("-emp-");

    public static final int START_VALUE_FOR_UNSPECIFIED_GE_TASK = 1;
    private String converterField;

    EmployeeTaskType(String converterField) {
        this.converterField = converterField;
    }

    public String getConverterField() {
        return converterField;
    }

    public static String getEmployeeTaskIdFromGeneralTaskId(String taskId, String employeeId) {
        return taskId + EmployeeTaskType.SPECIFIED_GE_TASK.getConverterField() + employeeId;
    }

    public static String getEmployeeTaskIdFromGeneralTaskId(String taskId, EmployeeWorkShift employeeWorkShift) {
        return getEmployeeTaskIdFromGeneralTaskId(taskId, employeeWorkShift.getWorkShiftId());
    }

    public static String getEmployeeTaskIdWithoutEmployee(String taskId, int number) {
        return taskId + EmployeeTaskType.UNSPECIFIED_GE_TASK.getConverterField() + number;
    }
}
