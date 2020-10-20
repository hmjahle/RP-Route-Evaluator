package com.visma.of.rp.routeevaluator.problemInstance.entities.address;

import aws.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import inputhandler.validation.coordinate.CoordinateValidation;
import inputhandler.validation.zipcode.ZipCodeValidation;
import probleminstance.ProblemInstance;
import probleminstance.entities.Configuration;
import probleminstance.entities.interfaces.EntityInterface;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Patient extends AddressEntity implements EntityInterface {


    @NotNull(message = "Patient id cannot be null") @NotBlank(message = "Patient id cannot be blank")
    @NotEmpty(message = "Patient id cannot be empty") @JsonProperty(value = "patientId")
    private String patientId;


    @JsonProperty
    private String patientName;

    public Patient(@NotBlank(message = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_NAME_NUMBER_AND_LETTER + " cannot be blank")
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_NAME_NUMBER_AND_LETTER, required = true) String address,
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_DESCRIPTION) String addressDescription,
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_PERMANENT_STREET_ADDRESS) Boolean permanentAddress,
                   @ZipCodeValidation @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_ZIPCODE) String zipCode,
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LATITUDE) @CoordinateValidation String latitude,
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LONGITUDE) @CoordinateValidation String longitude,
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_MUNICIPALITY_NAME)String municipalityName,
                   @NotNull(message = "Patient id cannot be null")
                   @NotBlank(message = "Patient id cannot be blank")
                   @NotEmpty(message = "Patient id cannot be empty")
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_ID, required = true)String patientId,
                   @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_PATIENT_NAME) String patientName) {
        super(address, addressDescription, permanentAddress, zipCode, latitude, longitude, municipalityName);
        this.patientId = patientId;
        this.patientName = patientName;
    }

    @Override
    public String getId() {
        return patientId;
    }


    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getId() +
                "latLong="  + super.getLatitude() + "," + super.getLongitude()+
                '}';
    }


    @Override
    @JsonIgnore
    public String getUniqueId() {
        return patientId;
    }

    @Override
    public void processRawData(Configuration configuration, ProblemInstance problemInstance) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(patientId, patient.patientId) &&
                Objects.equals(patientName, patient.patientName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), patientId, patientName);
    }

    public static Patient patientEmptyCreator(String address){
        return new Patient(address, null, null, null, null, null,
                null, null, null);
    }

}
