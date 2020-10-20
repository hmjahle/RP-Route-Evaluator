package com.visma.of.rp.routeevaluator.problemInstance;

import aws.constants.ApiConstants;
import aws.entity.ErrorAppender;
import aws.entity.responses.updateprobleminstance.TaskWorkShiftCompatibility;
import aws.errors.ErrorResponseSaver;
import aws.errors.detail.validation.*;
import aws.errors.parent.DataValidationError;
import aws.utils.DynamoDBUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import inputhandler.entityhelpers.ShiftCodeHelper;
import inputhandler.entityhelpers.TaskHelper;
import inputhandler.entityhelpers.WorkShiftHelper;
import inputhandler.validation.JSONMapperAndValidator;
import org.json.simple.JSONObject;
import probleminstance.entities.Configuration;
import probleminstance.entities.Parameters;
import probleminstance.entities.SkillLevel;
import probleminstance.entities.SyncedTaskPair;
import probleminstance.entities.address.OfficeInfo;
import probleminstance.entities.address.Patient;
import probleminstance.entities.employee.Employee;
import probleminstance.entities.interfaces.EntityInterface;
import probleminstance.entities.restrictions.PatientVisitRestriction;
import probleminstance.entities.restrictions.TaskToRequiredEmployees;
import probleminstance.entities.restrictions.VisitRestriction;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.WorkShift;
import probleminstance.entities.timeinterval.task.EmployeeTask;
import probleminstance.entities.timeinterval.task.GeneralTask;
import probleminstance.entities.timeinterval.task.PatientTask;
import probleminstance.entities.timeinterval.task.Task;
import probleminstance.entities.transport.TransportInformation;
import probleminstance.entities.visithistory.VisitHistoryEmployeePatientPair;
import probleminstance.initializers.workshifts.EmployeeStructure;
import utils.files.JSON;
import utils.maps.coordinates.AddressGenerator;

import javax.validation.ConstraintViolation;
import java.util.*;

public class ProblemInstance extends ProblemInstanceRaw {

    @JsonIgnore
    private Configuration configuration;
    @JsonIgnore
    private JSONObject originalJsonObject;
    @JsonIgnore
    private OfficeInfo officeInfo;
    @JsonIgnore
    private Map<String, Patient> patients;
    @JsonIgnore
    private Set<VisitRestriction> visitRestrictions;
    @JsonIgnore
    private ProblemInstanceHelpStructure problemInstanceHelpStructure;
    @JsonIgnore
    private ProblemInstanceTaskStructure problemInstanceTaskStructure;
    @JsonIgnore
    private Map<String, VisitHistoryEmployeePatientPair> visitHistoryEmployeePatientPairs;
    @JsonIgnore
    private EmployeeStructure employeeStructure;
    @JsonIgnore
    private ErrorAppender errorAppender;
    @JsonIgnore
    private DynamoDBUtils dynamoDBUtils;
    @JsonIgnore
    private String customerId;
    @JsonIgnore
    private Map<String, Patient> removedPatients;

    @JsonIgnore
    private Boolean considerHeavyTasks;

    @JsonCreator
    public ProblemInstance(
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENTS, required = true) List<Patient> patientsRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASKS, required = true) List<PatientTask> patientTasksRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_VISIT_RESTRICTIONS, required = true) List<PatientVisitRestriction> patientVisitRestrictionsRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_GENERAL_TASKS, required = true) List<GeneralTask> generalTasksRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TASK_TO_REQUIRED_EMPLOYEES) List<TaskToRequiredEmployees> taskToReqEmployeesRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_SKILL_LEVELS, required = true) List<SkillLevel> skillLevelsRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_WORKHOURS, required = true) List<WorkShift> workHoursRaw,
            @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_EMPLOYEE_TASKS, required = true) List<EmployeeTask> emTasksNotPatientsRaw,
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
        super(patientsRaw, patientTasksRaw, patientVisitRestrictionsRaw, generalTasksRaw, taskToReqEmployeesRaw,
                skillLevelsRaw, workHoursRaw, emTasksNotPatientsRaw, employeesRaw, officeInfoRaw,
                visitHistoryEmployeePatientPairs, shiftTypesRaw, syncedTaskPairs, employeeWorkShifts, parameters,
                pageId, jobId, taskWorkShiftCompatibilityList);
    }

    public void initialize(JSONObject originalJsonObject, Configuration configuration, ErrorAppender errorAppender, String customerId, DynamoDBUtils dynamoDBUtils) {
        this.originalJsonObject = originalJsonObject;
        this.configuration = configuration;
        this.errorAppender = errorAppender;
        this.customerId = customerId;
        this.dynamoDBUtils = dynamoDBUtils;
        this.considerHeavyTasks = true;
        initializeStructures(configuration, errorAppender);

    }

    public void initializeStructures(Configuration configuration, ErrorAppender errorAppender) {
        // Order is important. This is because each Class initializes other fields in other classes.
        // For instance, a PatientVisitRestriction adds the patient to an employee's whoNotToVisit list.
        problemInstanceTaskStructure = new ProblemInstanceTaskStructure(this);
        visitRestrictions = new HashSet<>();
        Set<ConstraintViolation<EntityInterface>> violations = new HashSet<>();
        Set<Task> tasks = getAllTasks(getPatientTasksRaw(), getEmployeeTasksRaw(), getGeneralTasksRaw());
        checkNoMultipleIdsIn(getPatientTasksRaw(), tasks, errorAppender);
        WorkShiftHelper.shiftTypeValidation(tasks, getShiftTypesRaw(), errorAppender, ShiftTypeNotDefinedForTaskError.class);
        ShiftCodeHelper.validateShiftCodes(this, errorAppender);
        setUpBasicStructures(violations);
        Parameters.updateParametersInConfigurationClass(configuration, getParameters());
        appendErrorIfErrorFound(violations, errorAppender);
        if (!errorAppender.hasError()) {
            setUpWorkShiftsAndEmployeeTasks(violations);
            setUpVisitHistory(violations);
            if (violations.isEmpty()) {
                problemInstanceTaskStructure.updateHeavyTaskSortedList();
                problemInstanceTaskStructure.setUpSyncedTasks();
            }
            appendErrorIfErrorFound(violations, errorAppender);
        }
    }

    private void setUpBasicStructures(Set<ConstraintViolation<EntityInterface>> violations) {
        officeInfo = JSON.generateObject(getOfficeInfoRaw(), violations, this, configuration);
        if (violations.isEmpty()) {
            AddressGenerator.updateOfficeInfoAddress(officeInfo, configuration, errorAppender, dynamoDBUtils);
        }
        if (violations.isEmpty()) {
            patients = JSON.generateObjectMap(getPatientsRaw(), violations, this, configuration);
        }
        if (violations.isEmpty()) {
            problemInstanceTaskStructure.setPatientTasks(this, getPatientTasksRaw(), violations);
        }
    }

    private void setUpWorkShiftsAndEmployeeTasks(Set<ConstraintViolation<EntityInterface>> violations) {
        employeeStructure = new EmployeeStructure(this, violations);
        if (violations.isEmpty()) {
            problemInstanceTaskStructure.setEmployeeTasks(this, getEmployeeTasksRaw(), violations);
        }
        if (violations.isEmpty()) {
            problemInstanceTaskStructure.setGeneralTasks(this, getGeneralTasksRaw(), violations);
        }
        if (violations.isEmpty()) {
            problemInstanceTaskStructure.setUpOriginalTaskIdToTaskToRequiredEmployee();
        }
        if (violations.isEmpty()) {
            JSON.generateObjectMap(getPatientVisitRestrictionsRaw(), violations, this, configuration);
        }
        if (violations.isEmpty()) {
            JSON.generateObjectMap(getTaskToReqEmployeesRaw(), violations, this, configuration);
        }
    }


    private void appendErrorIfErrorFound(Set<ConstraintViolation<EntityInterface>> violations, ErrorAppender errorAppender) {
        String violationMessage = getViolationMessages(violations);
        if (violationMessage.length() > 0) {
            ErrorResponseSaver.saveError(new DataValidationError(), new JsonValidationError(violationMessage), errorAppender, new Exception());
        }
    }

    public void setUpHelpDataStructuresWithoutTransportInformation(ErrorAppender errorAppender, boolean useTransportTimes) {
        if (!errorAppender.hasError()) {
            problemInstanceHelpStructure = new ProblemInstanceHelpStructure(this, errorAppender, useTransportTimes);
            problemInstanceHelpStructure.setUpHelpStructures();
        }
    }

    public void setUpHelpDataStructuresGivenTransportInformation(ErrorAppender errorAppender, TransportInformation transportInformation) {
        problemInstanceHelpStructure = new ProblemInstanceHelpStructure(this, errorAppender, transportInformation);
        problemInstanceHelpStructure.setUpHelpStructures();
    }

    private void setUpVisitHistory(Set<ConstraintViolation<EntityInterface>> violations) {
        visitHistoryEmployeePatientPairs = JSON.generateObjectMap(getVisitHistoryEmployeePatientPairsRaw(), violations, this, configuration);
    }

    private void checkNoMultipleIdsIn(Collection<PatientTask> patientTasks, Set<Task> tasks, ErrorAppender errorAppender) {
        TaskHelper.checkNoMultipleIdsIn(tasks, Task::getTaskId, errorAppender);
        TaskHelper.checkNoMultipleIdsIn(patientTasks, Task::getOriginalTaskId, errorAppender);
    }

    private Set<Task> getAllTasks(Collection<PatientTask> patientTasks, Collection<EmployeeTask> employeeTasks,
                             Collection<GeneralTask> generalTasks){
        Set<Task> tasks = new HashSet<>(patientTasks);
        tasks.addAll(employeeTasks);
        tasks.addAll(generalTasks);
        return tasks;
    }

    public static ProblemInstance initializeWithHelpStructure(Configuration conf, JSONObject problemInstanceJson,
                                                              ErrorAppender errorAppender, DynamoDBUtils dynamoDBUtils, String customerId, boolean useTransportInformation) {
        ProblemInstance problemInstance = initializeWithoutHelpStructure(conf, problemInstanceJson, errorAppender, dynamoDBUtils, customerId);
        if (!errorAppender.hasError()) {
            problemInstance.setUpHelpDataStructuresWithoutTransportInformation(errorAppender, useTransportInformation);
        }
        return problemInstance;
    }


    public static ProblemInstance initializeWithoutHelpStructure(Configuration conf, JSONObject problemInstanceJson,
                                                                 ErrorAppender errorAppender, DynamoDBUtils dynamoDBUtils, String customerId) {
        ProblemInstance problemInstance = JSONMapperAndValidator.parseJsonSingleObjectDataValidation(problemInstanceJson, ProblemInstance.class, errorAppender);
        if (problemInstance != null) {
            problemInstance.initialize(problemInstanceJson, conf, errorAppender, customerId, dynamoDBUtils);
        }
        return problemInstance;
    }

    private String getViolationMessages(Set<ConstraintViolation<EntityInterface>> violations) {
        String message = "";
        for (ConstraintViolation viol : violations) {
            message += viol.getPropertyPath() + " " + viol.getMessage() + "\n";
        }
        if (message.length() > 0) {
            message = message.substring(0, message.length() - 1);
        }
        return message;
    }

    public ProblemInstanceTaskStructure getProblemInstanceTaskStructure() {
        return problemInstanceTaskStructure;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @JsonIgnore
    public Map<String, Patient> getPatients() {
        return patients;
    }

    public void removePatient(String patientId) {
        patients.remove(patientId);
    }

    @JsonIgnore
    public OfficeInfo getOfficeInfo() {
        return officeInfo;
    }

    public ProblemInstanceHelpStructure getProblemInstanceHelpStructure() {
        return problemInstanceHelpStructure;
    }

    @JsonIgnore
    public Set<VisitRestriction> getVisitRestrictions() {
        return visitRestrictions;
    }

    public ErrorAppender getErrorAppender() {
        return errorAppender;
    }

    public JSONObject getOriginalJsonObject() {
        return originalJsonObject;
    }

    public Task getTask(String taskId) {
        return getProblemInstanceTaskStructure().getTask(taskId);
    }

    public EmployeeStructure getEmployeeStructure() {
        return employeeStructure;
    }

    public DynamoDBUtils getDynamoDBUtils() {
        return dynamoDBUtils;
    }

    public void setDynamoDBUtils(DynamoDBUtils dynamoDBUtils) {
        this.dynamoDBUtils = dynamoDBUtils;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Map<String, Patient> getRemovedPatients() {
        return removedPatients;
    }

    public void setRemovedPatients(Map<String, Patient> removedPatients) {
        this.removedPatients = removedPatients;
    }

    public Map<String, VisitHistoryEmployeePatientPair> getVisitHistoryEmployeePatientPairs() {
        return visitHistoryEmployeePatientPairs;
    }

    public Boolean getConsiderHeavyTasks() {
        return considerHeavyTasks;
    }

    public void setConsiderHeavyTasks(Boolean considerHeavyTasks) {
        this.considerHeavyTasks = considerHeavyTasks;
    }
}
