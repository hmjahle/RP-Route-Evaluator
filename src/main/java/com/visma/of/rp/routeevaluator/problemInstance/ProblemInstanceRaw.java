package com.visma.of.rp.routeevaluator.problemInstance;

import aws.constants.ApiConstants;
import aws.entity.responses.updateprobleminstance.TaskWorkShiftCompatibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import inputhandler.validation.time.DateTimeValidator;
import probleminstance.entities.Parameters;
import probleminstance.entities.SkillLevel;
import probleminstance.entities.SyncedTaskPair;
import probleminstance.entities.address.OfficeInfo;
import probleminstance.entities.address.Patient;
import probleminstance.entities.employee.Employee;
import probleminstance.entities.restrictions.PatientVisitRestriction;
import probleminstance.entities.restrictions.TaskToRequiredEmployees;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.TimeInterval;
import probleminstance.entities.timeinterval.WorkShift;
import probleminstance.entities.timeinterval.task.EmployeeTask;
import probleminstance.entities.timeinterval.task.GeneralTask;
import probleminstance.entities.timeinterval.task.PatientTask;
import probleminstance.entities.visithistory.VisitHistoryEmployeePatientPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProblemInstanceRaw {

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENTS, required = true)
    private List<Patient> patientsRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASKS, required = true)
    private List<PatientTask> patientTasksRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_VISIT_RESTRICTIONS, required = true)
    private List<PatientVisitRestriction> patientVisitRestrictionsRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASKS, required = true)
    private List<GeneralTask> generalTasksRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_TO_REQUIRED_EMPLOYEES)
    private List<TaskToRequiredEmployees> taskToReqEmployeesRaw = new ArrayList<>();
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SKILL_LEVELS, required = true)
    private List<SkillLevel> skillLevelsRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKHOURS, required = true)
    private List<WorkShift> workHoursRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASKS, required = true)
    private List<EmployeeTask> employeeTasksRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEES, required = true)
    private List<Employee> employeesRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO, required = true)
    private OfficeInfo officeInfoRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_VISIT_HISTORY)
    private List<VisitHistoryEmployeePatientPair> visitHistoryEmployeePatientPairsRaw = new ArrayList<>();
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPES)
    private List<String> shiftTypesRaw;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SYNCED_TASK_PAIRS)
    private List<SyncedTaskPair> syncedTaskPairs;
    @JsonProperty(value = ApiConstants.PARAMETERS)
    private Parameters parameters;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_WORK_SHIFTS)
    private List<EmployeeWorkShift> employeeWorkShifts;
    @JsonProperty(value = ApiConstants.JOB_ID)
    private String jobId;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PAGE_ID)
    private String pageId;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_COMPATIBILITY_MATRIX)
    private List<TaskWorkShiftCompatibility> taskWorkShiftCompatibilityList;

    @JsonCreator
    public ProblemInstanceRaw(
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENTS, required = true) List<Patient> patientsRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASKS, required = true) List<PatientTask> patientTasksRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_VISIT_RESTRICTIONS, required = true) List<PatientVisitRestriction> patientVisitRestrictionsRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASKS, required = true) List<GeneralTask> generalTasksRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_TO_REQUIRED_EMPLOYEES) List<TaskToRequiredEmployees> taskToReqEmployeesRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SKILL_LEVELS, required = true) List<SkillLevel> skillLevelsRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKHOURS, required = true) List<WorkShift> workHoursRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASKS, required = true) List<EmployeeTask> employeeTasksRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEES, required = true) List<Employee> employeesRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO, required = true) OfficeInfo officeInfoRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_VISIT_HISTORY) List<VisitHistoryEmployeePatientPair> visitHistoryEmployeePatientPairs,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SHIFT_TYPES) List<String> shiftTypesRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SYNCED_TASK_PAIRS) List<SyncedTaskPair> syncedTaskPairs,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_WORK_SHIFTS) List<EmployeeWorkShift> employeeWorkShifts,
            @JsonProperty(value = ApiConstants.PARAMETERS) Parameters parameters,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PAGE_ID) String pageId,
            @JsonProperty(value = ApiConstants.JOB_ID) String jobId,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_COMPATIBILITY_MATRIX) List<TaskWorkShiftCompatibility> taskWorkShiftCompatibilityList) {
        this.patientsRaw = patientsRaw;
        this.patientTasksRaw = patientTasksRaw;
        this.patientVisitRestrictionsRaw = patientVisitRestrictionsRaw;
        this.generalTasksRaw = generalTasksRaw;
        this.skillLevelsRaw = skillLevelsRaw;
        this.workHoursRaw = workHoursRaw;
        this.employeeTasksRaw = employeeTasksRaw;
        this.employeesRaw = employeesRaw;
        this.officeInfoRaw = officeInfoRaw;
        this.shiftTypesRaw = shiftTypesRaw;
        this.syncedTaskPairs = syncedTaskPairs;
        this.employeeWorkShifts = employeeWorkShifts;
        this.parameters = parameters;
        this.jobId = jobId;
        this.pageId = pageId;
        this.taskWorkShiftCompatibilityList = taskWorkShiftCompatibilityList;
        if(visitHistoryEmployeePatientPairs != null){
            this.visitHistoryEmployeePatientPairsRaw = visitHistoryEmployeePatientPairs;
        }
        if(taskToReqEmployeesRaw != null){
            this.taskToReqEmployeesRaw = taskToReqEmployeesRaw;
        }
        processAllTimeIntervals();
    }

    private void processAllTimeIntervals(){
        Set<TimeInterval> timeIntervalSet = initializeTimeIntervalSet();
        for (TimeInterval timeInterval : timeIntervalSet){
            timeInterval.setFromTime(DateTimeValidator.addZeroesToMatchLength(timeInterval.getFromTime(), 4));
            timeInterval.setToTime(DateTimeValidator.addZeroesToMatchLength(timeInterval.getToTime(), 4));
        }
    }


    private Set<TimeInterval> initializeTimeIntervalSet(){
        Set<TimeInterval> timeIntervalSet = new HashSet<>();
        timeIntervalSet.addAll(patientTasksRaw);
        timeIntervalSet.addAll(generalTasksRaw);
        timeIntervalSet.addAll(workHoursRaw);
        timeIntervalSet.addAll(employeeTasksRaw);
        return timeIntervalSet;
    }

    public List<Patient> getPatientsRaw() {
        return patientsRaw;
    }

    public List<PatientTask> getPatientTasksRaw() {
        return patientTasksRaw;
    }

    public List<PatientVisitRestriction> getPatientVisitRestrictionsRaw() {
        return patientVisitRestrictionsRaw;
    }

    public List<GeneralTask> getGeneralTasksRaw() {
        return generalTasksRaw;
    }

    public List<TaskToRequiredEmployees> getTaskToReqEmployeesRaw() {
        return taskToReqEmployeesRaw;
    }

    public List<SkillLevel> getSkillLevelsRaw() {
        return skillLevelsRaw;
    }

    public List<WorkShift> getWorkHoursRaw() {
        return workHoursRaw;
    }

    public List<EmployeeTask> getEmployeeTasksRaw() {
        return employeeTasksRaw;
    }

    public List<Employee> getEmployeesRaw() {
        return employeesRaw;
    }

    public OfficeInfo getOfficeInfoRaw() {
        return officeInfoRaw;
    }

    public List<VisitHistoryEmployeePatientPair> getVisitHistoryEmployeePatientPairsRaw() {
        return visitHistoryEmployeePatientPairsRaw;
    }

    public void setVisitHistoryEmployeePatientPairsRaw(List<VisitHistoryEmployeePatientPair> visitHistoryEmployeePatientPairsRaw) {
        this.visitHistoryEmployeePatientPairsRaw = visitHistoryEmployeePatientPairsRaw;
    }

    public List<String> getShiftTypesRaw() {
        return shiftTypesRaw;
    }

    public List<SyncedTaskPair> getSyncedTaskPairs() {
        return syncedTaskPairs;
    }
    public Parameters getParameters() {
        return parameters;
    }
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }


    public List<EmployeeWorkShift> getEmployeeWorkShifts() {
        return employeeWorkShifts;
    }

    public void setEmployeeWorkShifts(List<EmployeeWorkShift> employeeWorkShifts) {
        this.employeeWorkShifts = employeeWorkShifts;
    }

    public void removeWorkShiftsAndEmployees(){
        workHoursRaw = new ArrayList<>();
        employeesRaw = new ArrayList<>();
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setPatientTasksRaw(List<PatientTask> patientTasksRaw) {
        this.patientTasksRaw = patientTasksRaw;
    }

    public void setTaskWorkShiftCompatibilityList(List<TaskWorkShiftCompatibility> taskWorkShiftCompatibilityList) {
        this.taskWorkShiftCompatibilityList = taskWorkShiftCompatibilityList;
    }

    public List<TaskWorkShiftCompatibility> getTaskWorkShiftCompatibilityList() {
        return taskWorkShiftCompatibilityList;
    }
}
