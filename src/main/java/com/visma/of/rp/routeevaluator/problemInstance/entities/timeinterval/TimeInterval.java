package com.visma.of.rp.routeevaluator.problemInstance.entities.timeinterval;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import inputhandler.validation.time.ApiDateFormat;
import inputhandler.validation.time.DateTimeValidation;
import inputhandler.validation.time.DateTimeValidator;
import probleminstance.ProblemInstance;
import utils.comparator.CompareMethods;
import utils.dateTime.DateHandler;

import javax.validation.constraints.NotEmpty;

public class TimeInterval implements Comparable<TimeInterval> {

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION)
    private Double durationMinutes;

    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_DATE)
    @DateTimeValidation(dateFormatString = ApiDateFormat.dateFormat)
    protected String fromDate;

    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_DATE)
    @DateTimeValidation(dateFormatString = ApiDateFormat.dateFormat)
    protected String toDate;

    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_TIME)
    @DateTimeValidation(dateFormatString = ApiDateFormat.timeFormat)
    protected String fromTime;

    @NotEmpty
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_TIME)
    @DateTimeValidation(dateFormatString = ApiDateFormat.timeFormat)
    protected String toTime;

    @JsonIgnore
    protected long startTime;
    @JsonIgnore
    protected long endTime;
    @JsonIgnore
    protected long durationSeconds;
    @JsonIgnore
    private DateHandler dateHandler;

    @JsonCreator
    public TimeInterval(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_DURATION) Double durationMinutes,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_DATE, required = true) String fromDate,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_DATE, required = true) String toDate,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_FROM_TIME, required = true) String fromTime,
                        @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_TIME_INTERVAL_TO_TIME, required = true) String toTime) {
        this.durationMinutes = durationMinutes;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public TimeInterval(long startTime, long endTime) {
        setup(startTime, endTime, endTime - startTime);
    }

    public TimeInterval(long startTime, long endTime, DateHandler dateHandler) {
        this.dateHandler = dateHandler;
        setup(startTime, endTime, endTime - startTime);
    }

    public TimeInterval(long startTime, long endTime, long durationSeconds) {
        setup(startTime, endTime, durationSeconds);
    }

    private void setup(long startTime, long endTime, long durationSeconds){
        if (dateHandler == null){
            dateHandler = DateHandler.defaultConstuctor();
        }
        setStartTime(startTime);
        setEndTime(endTime);
        fromDate = dateHandler.getTimeStringFromSeconds(startTime, ApiDateFormat.dateFormat);
        fromTime = dateHandler.getTimeStringFromSeconds(startTime, ApiDateFormat.timeFormat);
        toDate = dateHandler.getTimeStringFromSeconds(endTime, ApiDateFormat.dateFormat);
        toTime = dateHandler.getTimeStringFromSeconds(endTime, ApiDateFormat.timeFormat);
        setDurationSeconds(durationSeconds);
    }

    public long getStartTime() {
        return startTime;
    }

    @JsonIgnore
    public long getLatestStartTime() { return endTime - durationSeconds; }

    public long getEndTime() {
        return endTime;
    }

    @JsonIgnore
    public long getEarliestEndTime() { return startTime + durationSeconds; }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public void setDurationSeconds(long durationSeconds){
        this.durationSeconds = durationSeconds;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public Double getDurationMinutes() {
        return durationMinutes;
    }

    public boolean hasNoSlack() { return endTime - startTime == durationSeconds; }

    public boolean contains(long time){
        return startTime <= time && time <= endTime;
    }

    @Override
    public int compareTo(TimeInterval o) {
        return CompareMethods.compareTimeIntervalStartAndEndTime(this, o);
    }

    @Override
    public String toString() {
        return "TimeInterval{" +
                "startTime=" + dateHandler.getTimeStringFromSeconds(startTime) +
                ", endTime=" + dateHandler.getTimeStringFromSeconds(endTime) +
                ", durationMinutes=" + durationMinutes +
                '}';
    }

    public void processRawData(ProblemInstance problemInstance) {
        dateHandler = problemInstance.getOfficeInfo().getDateHandler();
        updateStartAndEndTimeAndDuration();
    }

    public static Long getTimeInSeconds(DateHandler dateHandler, String date, String time){
        return dateHandler.getTimeInSecondsFromDateAndHours(date,
                DateTimeValidator.addZeroesToMatchLength(time, 4).replace(":", "")
                , ApiDateFormat.dateAndTimeFormat);
    }

    public void updateStartAndEndTimeAndDuration(){
        this.setStartTime(getTimeInSeconds(dateHandler, fromDate, fromTime));
        this.setEndTime(getTimeInSeconds(dateHandler, toDate, toTime));
        setDuration();
    }

    private void setDuration() {
        if (durationMinutes == null) {
            durationMinutes = Math.max((double)(getEndTime() - getStartTime()) / 60.0, 0);
        }
        this.durationSeconds = Math.round(durationMinutes * 60.0);
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public DateHandler getDateHandler() {
        return dateHandler;
    }

    public void setDateHandler(DateHandler dateHandler) {
        this.dateHandler = dateHandler;
    }
}

