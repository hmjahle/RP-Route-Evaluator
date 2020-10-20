package com.visma.of.rp.routeevaluator.problemInstance.entities.address;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OfficeInfo extends AddressEntity implements EntityInterface {

    @NotNull(message = "Office id cannot be null")
    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_ID)
    private String officeInfoId;
    @Min(0)
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_MINUTES_SLACK_ON_PATIENT_TASKS)
    private Integer minutesSlackOnPatientTasks;
    @Min(0)
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_NUMBER_OF_DAYS_USED_IN_VISIT_HISTORY)
    private Integer numberOfDaysUsedInVisitHistory;
    @Min(0)
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_MAX_NUMBER_OF_EMPLOYEES_IN_VISIT_HISTORY)
    private Integer maxNumberOfEmployeesInVisitHistory;
    @Min(1)
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_MAX_NUMBER_OF_MAIN_RESPONSIBILES)
    private Integer numberOfMainResponsiblesInVisitHistory;
    @Min(0)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_ROBUST_TIME_MINUTES)
    private Double robustTimeMinutes;
    @Min(0)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_PARKING_TIME_MINUTES)
    private Double parkingTimeMinutes;
    @Min(0)
    @Max(15)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_SOLVER_RUN_TIME_MINUTES)
    private Double solverRunTimeMinutes;
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_MAXIMUM_NUMBER_OF_HEAVY_TASKS_PER_EMPLOYEE)
    private Integer maximumNumberOfHeavyTasksPerEmployee;
    @Min(0)
    @Max(3)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_TRAVEL_TIME_WEIGHT_STRING)
    private Double travelTimeWeightProportion;
    @Min(0)
    @Max(3)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_TIME_WINDOW_WEIGHT_STRING)
    private Double timeWindowWeightProportion;
    @Min(0)
    @Max(3)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_VISIT_HISTORY_WEIGHT_STRING)
    private Double visitHistoryWeightProportion;
    @Min(0)
    @Max(3)
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_OVER_QUALIFIED_STRING)
    private Double overQualifiedWeightProportion;
    @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_USE_WORK_BALANCE)
    private Boolean useWorkBalance;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_AVAILABLE_CARS)
    private Integer availableCars;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_THROW_ERROR)
    private Boolean throwError;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_PLAN_NAME)
    private String planName;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_DEFAULT_TRANSPORT_MODE)
    private Transport defaultTransportMode;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_TIME_ZONE)
    @TimeZoneValidation
    private String timeZone;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_PLAN_DATE)
    @DateTimeValidation(dateFormatString = ApiDateFormat.dateFormat)
    private String planDate;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_PATIENT_TASK_DURATION_ADJUSTMENT_PROPORTION)
    @Min(-1)
    private Double patientTaskDurationAdjustmentPercentage;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_NUMBER_OF_PARALLEL_SOLVERS)
    @Min(1)
    @Max(10)
    private Integer numberOfParallelSolvers;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_USE_EUCLIDEAN_DISTANCES_FOR_TRAVEL_INFORMATION)
    private Boolean useEuclideanDistancesForTravelInformation;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_USE_FINALIZER)
    private Boolean useFinalizer;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_SIMULATED_ANNEALING_PERCENT_WORSE_SOLUTION_TO_ACCEPT)
    private Double simulatedAnnealingCriteria;
    @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_CLUSTER_THRESHOLD_VALUE)
    private Double clusterThreshold;
    @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_OVERTIME_IS_HARD_CONSTRAINT)
    private Boolean overtimeIsHardConstraint;
    @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_STRICT_TIME_WINDOWS_IS_HARD_CONSTRAINT)
    private Boolean strictTimeWindowsIsHardConstraint;
    @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_SYNCED_START_TIME_IS_HARD_CONSTRAINT)
    private Boolean syncedStartTimeIsHardConstraint;
    @JsonIgnore
    private DateHandler dateHandler;
    @JsonIgnore
    private Double solverRunTimeSeconds;
    @JsonIgnore
    private Integer taskSlackInSeconds;
    @JsonIgnore
    private Integer robustTimeSeconds;
    @JsonIgnore
    private Integer parkingTimeSeconds;
    @JsonIgnore
    private Double travelTimeProportionNormalized;
    @JsonIgnore
    private Double timeWindowProportionNormalized;
    @JsonIgnore
    private Double visitHistoryProportionNormalized;
    @JsonIgnore
    private Double overQualifiedProportionNormalized;

    @JsonIgnore
    private final Double DEFAULT_SOLVER_RUN_TIME_MINUTES = 5.0;
    @JsonIgnore
    private final Integer DEFAULT_TASK_SLACK_IN_SECONDS = 0;
    @JsonIgnore
    private final Double DEFAULT_ROBUST_TIME_MINUTES = 0.0;
    @JsonIgnore
    private final Double DEFAULT_PARKING_TIME_MINUTES = 1.0;
    @JsonIgnore
    private final Integer DEFAULT_NUMBER_OF_MAIN_RESPONSIBILITIES_VISIT_HISTORY = Integer.MAX_VALUE;
    @JsonIgnore
    private final Integer DEFAULT_MAX_NUMBER_OF_DIFFERENT_EMPLOYEES_VISIT_HISTORY = Integer.MAX_VALUE;
    @JsonIgnore
    private final Double DEFAULT_PATIENT_TASK_DURATION_ADJUSTMENT_PROPORTION = 0.0;
    @JsonIgnore
    private final Integer DEFAULT_MAX_NUMBER_OF_HEAVY_TASKS_PER_EMPLOYEE = Integer.MAX_VALUE;
    @JsonIgnore
    private final Integer DEFAULT_NUMBER_OF_PARALLEL_SOLVERS = 4;
    @JsonIgnore
    private final Boolean DEFAULT_USE_EUCLIDEAN_DISTANCES_FOR_TRAVEL_INFORMATION = false;
    @JsonIgnore
    private final Boolean DEFAULT_USE_FINALIZER = true;
    @JsonIgnore
    private final Double DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_SIMULATED_ANNEALING_PERCENT_WORSE_SOLUTION_TO_ACCEPT = 0.25;
    @JsonIgnore
    private final Double DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_IS_CLUSTER_THRESHOLD_VALUE = 1.4;
    @JsonIgnore
    private final Boolean DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_OVERTIME_IS_HARD_CONSTRAINT = false;
    @JsonIgnore
    private final Boolean DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_SYNCED_START_TIME_IS_HARD_CONSTRAINT = false;
    @JsonIgnore
    private final Boolean DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_STRICT_TIME_WINDOWS_IS_HARD_CONSTRAINT = false;

    @JsonCreator
    public OfficeInfo(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_NAME_NUMBER_AND_LETTER, required = true) String address,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_DESCRIPTION) String addressDescription,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_PERMANENT_STREET_ADDRESS) Boolean permanentAddress,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_ZIPCODE, required = true) String zipCode,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LATITUDE) String latitude,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LONGITUDE) String longitude,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_MUNICIPALITY_NAME) String municipalityName,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_ID, required = true) String officeInfoId,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_MINUTES_SLACK_ON_PATIENT_TASKS) Integer minutesSlackOnPatientTasks,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_NUMBER_OF_DAYS_USED_IN_VISIT_HISTORY) Integer numberOfDaysUsedInVisitHistory,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_MAX_NUMBER_OF_EMPLOYEES_IN_VISIT_HISTORY) Integer maxNumberOfEmployeesInVisitHistory,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_MAX_NUMBER_OF_MAIN_RESPONSIBILES) Integer numberOfMainResponsiblesInVisitHistory,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_ROBUST_TIME_MINUTES) Double robustTimeMinutes,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_SOLVER_RUN_TIME_MINUTES) Double solverRunTimeMinutes,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_TRAVEL_TIME_WEIGHT_STRING) Double travelTimeWeightProportion,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_TIME_WINDOW_WEIGHT_STRING) Double timeWindowWeightProportion,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_VISIT_HISTORY_WEIGHT_STRING) Double visitHistoryWeightProportion,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_OVER_QUALIFIED_STRING) Double overQualifiedWeightProportion,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_USE_WORK_BALANCE) Boolean useWorkBalance,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_AVAILABLE_CARS) Integer availableCars,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_THROW_ERROR) Boolean throwError,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_PLAN_NAME) String planName,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_DEFAULT_TRANSPORT_MODE) Transport defaultTransportMode,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_TIME_ZONE) String timeZone,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_PLAN_DATE) String planDate,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_PATIENT_TASK_DURATION_ADJUSTMENT_PROPORTION) Double patientTaskDurationAdjustmentPercentage,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_MAXIMUM_NUMBER_OF_HEAVY_TASKS_PER_EMPLOYEE) Integer maximumNumberOfHeavyTasksPerEmployee,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_NUMBER_OF_PARALLEL_SOLVERS) Integer numberOfParallelSolvers,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_USE_EUCLIDEAN_DISTANCES_FOR_TRAVEL_INFORMATION) Boolean useEuclideanDistancesForTravelInformation,
                      @JsonProperty(value = ApiConstants.OPTIMIZATION_CONFIGURATIONS_PARKING_TIME_MINUTES) Double parkingTimeMinutes,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_USE_FINALIZER) Boolean useFinalizer,
                      @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_OFFICEINFO_SIMULATED_ANNEALING_PERCENT_WORSE_SOLUTION_TO_ACCEPT) Double simulatedAnnealingCriteria,
                      @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_OVERTIME_IS_HARD_CONSTRAINT) Boolean overtimeIsHardConstraint,
                      @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_STRICT_TIME_WINDOWS_IS_HARD_CONSTRAINT) Boolean strictTimeWindowsIsHardConstraint,
                      @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_SYNCED_START_TIME_IS_HARD_CONSTRAINT) Boolean syncedStartTimeIsHardConstraint,
                      @JsonProperty(value = ApiConstants.DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_CLUSTER_THRESHOLD_VALUE) Double clusterThreshold) {
        super(address, addressDescription, permanentAddress, zipCode, latitude, longitude, municipalityName);
        this.officeInfoId = officeInfoId;
        this.minutesSlackOnPatientTasks = minutesSlackOnPatientTasks;
        this.numberOfDaysUsedInVisitHistory = numberOfDaysUsedInVisitHistory;
        this.maxNumberOfEmployeesInVisitHistory = maxNumberOfEmployeesInVisitHistory;
        this.numberOfMainResponsiblesInVisitHistory = numberOfMainResponsiblesInVisitHistory;
        this.robustTimeMinutes = robustTimeMinutes;
        this.parkingTimeMinutes = parkingTimeMinutes;
        this.solverRunTimeMinutes = solverRunTimeMinutes;
        this.travelTimeWeightProportion = travelTimeWeightProportion;
        this.timeWindowWeightProportion = timeWindowWeightProportion;
        this.visitHistoryWeightProportion = visitHistoryWeightProportion;
        this.overQualifiedWeightProportion = overQualifiedWeightProportion;
        this.useWorkBalance = useWorkBalance;
        this.availableCars = availableCars;
        this.throwError = throwError;
        this.planName = planName;
        this.defaultTransportMode = defaultTransportMode;
        this.timeZone = timeZone;
        this.planDate = planDate;
        this.patientTaskDurationAdjustmentPercentage = patientTaskDurationAdjustmentPercentage;
        this.maximumNumberOfHeavyTasksPerEmployee = maximumNumberOfHeavyTasksPerEmployee;
        this.numberOfParallelSolvers = numberOfParallelSolvers;
        this.dateHandler = new DateHandler(timeZone);
        this.useEuclideanDistancesForTravelInformation = useEuclideanDistancesForTravelInformation;
        this.useFinalizer = useFinalizer;
        this.simulatedAnnealingCriteria = simulatedAnnealingCriteria;
        this.clusterThreshold = clusterThreshold;
        this.overtimeIsHardConstraint = overtimeIsHardConstraint;
        this.strictTimeWindowsIsHardConstraint = strictTimeWindowsIsHardConstraint;
        this.syncedStartTimeIsHardConstraint = syncedStartTimeIsHardConstraint;
    }

    @Override
    public String getId() {
        return officeInfoId;
    }

    public String getOfficeDBIdentifier(Configuration configuration, ErrorAppender errorAppender, DynamoDBUtils dynamoDBUtils, String customerId) {
        if (super.getMunicipalityName() == null) {
            AddressGenerator.updateOfficeInfoAddress(this, configuration, errorAppender, dynamoDBUtils);
        }
        return getOfficeDBIdentifier(super.getMunicipalityName(), officeInfoId, customerId);
    }

    private static String getOfficeDBIdentifier(String municipalityName, String officeInfoId, String customerId) {
        return customerId + "," + municipalityName + "," + officeInfoId;
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return officeInfoId;
    }

    @Override
    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
        if (minutesSlackOnPatientTasks != null) {
            taskSlackInSeconds = minutesSlackOnPatientTasks * 60;
        } else {
            taskSlackInSeconds = DEFAULT_TASK_SLACK_IN_SECONDS;
        }
        if (numberOfMainResponsiblesInVisitHistory == null) {
            numberOfMainResponsiblesInVisitHistory = DEFAULT_NUMBER_OF_MAIN_RESPONSIBILITIES_VISIT_HISTORY;
        }
        if (maxNumberOfEmployeesInVisitHistory == null) {
            maxNumberOfEmployeesInVisitHistory = DEFAULT_MAX_NUMBER_OF_DIFFERENT_EMPLOYEES_VISIT_HISTORY;
        }
        setRobustTimeMinutes(robustTimeMinutes);
        setParkingTimeMinutes(parkingTimeMinutes);
        setSolverRunTimeMinutes(solverRunTimeMinutes);
        if (travelTimeWeightProportion == null) {
            travelTimeWeightProportion = 2.0;
        }
        if (timeWindowWeightProportion == null) {
            timeWindowWeightProportion = 2.0;
        }
        if (visitHistoryWeightProportion == null) {
            visitHistoryWeightProportion = 2.0;
        }
        if (overQualifiedWeightProportion == null) {
            overQualifiedWeightProportion = 0.0;
        }
        updateNormalizedObjectiveProportions();
        if (useWorkBalance == null) {
            useWorkBalance = false;
        }
        if (maximumNumberOfHeavyTasksPerEmployee == null) {
            maximumNumberOfHeavyTasksPerEmployee = DEFAULT_MAX_NUMBER_OF_HEAVY_TASKS_PER_EMPLOYEE;
        }
        if (throwError == null) {
            throwError = false;
        }
        if (availableCars == null) {
            availableCars = 0;
        }
        if (maxNumberOfEmployeesInVisitHistory == null) {
            maxNumberOfEmployeesInVisitHistory = 10;
        }
        if (planName == null) {
            planName = "";
        }
        if (planDate == null) {
            planDate = DateHandler.findMostCommonFromDate(problemInstance.getPatientTasksRaw());
        }
        if (defaultTransportMode == null) {
            defaultTransportMode = Transport.DRIVE;
        }
        if (patientTaskDurationAdjustmentPercentage == null) {
            patientTaskDurationAdjustmentPercentage = DEFAULT_PATIENT_TASK_DURATION_ADJUSTMENT_PROPORTION;
        }
        if (numberOfParallelSolvers == null) {
            numberOfParallelSolvers = DEFAULT_NUMBER_OF_PARALLEL_SOLVERS;
        }
        if (useEuclideanDistancesForTravelInformation == null) {
            useEuclideanDistancesForTravelInformation = DEFAULT_USE_EUCLIDEAN_DISTANCES_FOR_TRAVEL_INFORMATION;
        }
        if (useFinalizer == null) {
            useFinalizer = DEFAULT_USE_FINALIZER;
        }
        if (simulatedAnnealingCriteria == null)
            simulatedAnnealingCriteria = DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_SIMULATED_ANNEALING_PERCENT_WORSE_SOLUTION_TO_ACCEPT;
        if (clusterThreshold == null)
            clusterThreshold = DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_IS_CLUSTER_THRESHOLD_VALUE;
        if (overtimeIsHardConstraint == null)
            overtimeIsHardConstraint = DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_OVERTIME_IS_HARD_CONSTRAINT;
        if (syncedStartTimeIsHardConstraint == null)
            syncedStartTimeIsHardConstraint = DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_SYNCED_START_TIME_IS_HARD_CONSTRAINT;
        if (strictTimeWindowsIsHardConstraint == null)
            strictTimeWindowsIsHardConstraint = DEFAULT_PROBLEM_INSTANCE_OFFICEINFO_STRICT_TIME_WINDOWS_IS_HARD_CONSTRAINT;
    }

    public <T> void setObjectiveWeightAndUpdateNormalizedObjectiveProportions(Consumer<T> consumer, T weight){
        if (weight != null){
            consumer.accept(weight);
            updateNormalizedObjectiveProportions();
        }
    }

    public void setTravelTimeWeightProportion(Double travelTimeWeightProportion) {
        this.travelTimeWeightProportion = travelTimeWeightProportion;
    }

    public void setTimeWindowWeightProportion(Double timeWindowWeightProportion) {
        this.timeWindowWeightProportion = timeWindowWeightProportion;
    }

    public void setVisitHistoryWeightProportion(Double visitHistoryWeightProportion) {
        this.visitHistoryWeightProportion = visitHistoryWeightProportion;
    }

    public void setOverQualifiedWeightProportion(Double overQualifiedWeightProportion) {
        this.overQualifiedWeightProportion = overQualifiedWeightProportion;
    }

    public Double getSolverRunTimeMinutes() {
        return solverRunTimeMinutes;
    }

    public Double getSolverRunTimeSeconds() {
        return solverRunTimeSeconds;
    }

    public Double getTravelTimeWeightProportion() {
        return travelTimeWeightProportion;
    }

    public Double getTimeWindowWeightProportion() {
        return timeWindowWeightProportion;
    }

    public Double getVisitHistoryWeightProportion() {
        return visitHistoryWeightProportion;
    }

    public Double getOverQualifiedWeightProportion() {
        return overQualifiedWeightProportion;
    }

    public Double getSimulatedAnnealingCriteria() {
        return simulatedAnnealingCriteria;
    }

    public Double getClusterThreshold() {
        return clusterThreshold;
    }

    public boolean getOvertimeIsHardConstraint() {
        return overtimeIsHardConstraint;
    }

    public boolean getStrictTimeWindowsIsHardConstraint() {
        return strictTimeWindowsIsHardConstraint;
    }

    public boolean getSyncedStartTimeIsHardConstraint() {
        return syncedStartTimeIsHardConstraint;
    }

    public Boolean getUseWorkBalance() {
        return useWorkBalance;
    }

    public void setUseWorkBalance(Boolean useWorkBalance) {
        this.useWorkBalance = useWorkBalance;
    }

    public boolean isThrowError() {
        return throwError;
    }

    public Transport getDefaultTransportMode() {
        return defaultTransportMode;
    }

    public String getPlanDate() {
        return planDate;
    }

    public Double getPatientTaskDurationAdjustmentPercentage() {
        return patientTaskDurationAdjustmentPercentage;
    }

    public void setPatientTaskDurationAdjustmentPercentage(Double patientTaskDurationAdjustmentPercentage) {
        this.patientTaskDurationAdjustmentPercentage = patientTaskDurationAdjustmentPercentage;
    }

    public DateHandler getDateHandler() {
        return dateHandler;
    }

    public Integer getMaximumNumberOfHeavyTasksPerEmployee() {
        return maximumNumberOfHeavyTasksPerEmployee;
    }

    public void setMaximumNumberOfHeavyTasksPerEmployee(Integer maximumNumberOfHeavyTasksPerEmployee) {
        this.maximumNumberOfHeavyTasksPerEmployee = maximumNumberOfHeavyTasksPerEmployee;
    }

    public void setSolverRunTimeMinutes(Double solverRunTimeMinutes) {
        if (solverRunTimeMinutes == null) {
            solverRunTimeMinutes = DEFAULT_SOLVER_RUN_TIME_MINUTES;
        }
        this.solverRunTimeMinutes = solverRunTimeMinutes;
        this.solverRunTimeSeconds = solverRunTimeMinutes * 60.0;
    }

    public void setSolverRunTimeSeconds(double solverRunTimeSeconds) {
        this.solverRunTimeSeconds = solverRunTimeSeconds;
        this.solverRunTimeMinutes = solverRunTimeSeconds / 60.0;
    }

    public Integer getTaskSlackInSeconds() {
        return taskSlackInSeconds;
    }

    public Integer getRobustTimeSeconds() {
        return robustTimeSeconds;
    }

    public Integer getNumberOfMainResponsiblesInVisitHistory() {
        return numberOfMainResponsiblesInVisitHistory;
    }

    public Integer getMaxNumberOfEmployeesInVisitHistory() {
        return maxNumberOfEmployeesInVisitHistory;
    }

    public Integer getNumberOfParallelSolvers() {
        return numberOfParallelSolvers;
    }

    public void setNumberOfParallelSolvers(Integer numberOfParallelSolvers) {
        this.numberOfParallelSolvers = numberOfParallelSolvers;
    }

    public Boolean getUseEuclideanDistancesForTravelInformation() {
        return useEuclideanDistancesForTravelInformation;
    }


    private void updateNormalizedObjectiveProportions() {
        int roundingDecimals = 2;
        List<Double> objectives = new ArrayList<>();
        objectives.add(travelTimeWeightProportion);
        objectives.add(timeWindowWeightProportion);
        objectives.add(visitHistoryWeightProportion);
        objectives.add(overQualifiedWeightProportion);
        MathHelper.scaleSumOfPositiveNumbersTo(objectives, 1.0);
        travelTimeProportionNormalized = MathHelper.round(objectives.get(0), roundingDecimals);
        timeWindowProportionNormalized = MathHelper.round(objectives.get(1), roundingDecimals);
        visitHistoryProportionNormalized = MathHelper.round(objectives.get(2), roundingDecimals);
        overQualifiedProportionNormalized = MathHelper.round(objectives.get(3), roundingDecimals);
    }

    public Double getTravelTimeProportionNormalized() {
        return travelTimeProportionNormalized;
    }

    public Double getTimeWindowProportionNormalized() {
        return timeWindowProportionNormalized;
    }

    public Double getVisitHistoryProportionNormalized() {
        return visitHistoryProportionNormalized;
    }

    public Double getOverQualifiedProportionNormalized() {
        return overQualifiedProportionNormalized;
    }

    public void setOvertimeIsHardConstraint(boolean isHardConstraint) {
        overtimeIsHardConstraint = isHardConstraint;
    }

    public void setStrictTimeWindowsIsHardConstraint(boolean isHardConstraint) {
        strictTimeWindowsIsHardConstraint = isHardConstraint;
    }

    public void setSyncedStartTimeIsHardConstraint(boolean isHardConstraint) {
        syncedStartTimeIsHardConstraint = isHardConstraint;
    }

    public void setRobustTimeMinutes(Double robustTimeMinutes) {
        if (robustTimeMinutes == null) {
            robustTimeMinutes = DEFAULT_ROBUST_TIME_MINUTES;
        }
        this.robustTimeMinutes = robustTimeMinutes;
        this.robustTimeSeconds = (int) (robustTimeMinutes * 60);
    }

    public void setParkingTimeMinutes(Double parkingTimeMinutes) {
        if (parkingTimeMinutes == null) {
            parkingTimeMinutes = DEFAULT_PARKING_TIME_MINUTES;
        }
        this.parkingTimeMinutes = parkingTimeMinutes;
        this.parkingTimeSeconds = (int) (parkingTimeMinutes * 60);
    }

    public void setParkingTimeSeconds(int parkingTimeSeconds) {
        this.parkingTimeSeconds = parkingTimeSeconds;
        this.parkingTimeMinutes = (double) parkingTimeSeconds / 60;
    }

    public Integer getParkingTimeSeconds() {
        return parkingTimeSeconds;
    }

    public Double getParkingTimeMinutes() {
        return parkingTimeMinutes;
    }

    public Double getRobustTimeMinutes() {
        return robustTimeMinutes;
    }

    public Boolean getUseFinalizer() {
        return useFinalizer;
    }
}
