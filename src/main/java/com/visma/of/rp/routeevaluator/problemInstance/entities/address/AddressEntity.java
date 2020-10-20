package com.visma.of.rp.routeevaluator.problemInstance.entities.address;


import java.util.Objects;

public abstract class AddressEntity {

    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_NAME_NUMBER_AND_LETTER, required = true)
    private String address;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_DESCRIPTION)
    private String addressDescription;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_PERMANENT_STREET_ADDRESS)
    private Boolean addressIsPermanent;
    @ZipCodeValidation
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_ZIPCODE)
    private String zipCode;
    @CoordinateValidation
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LATITUDE)
    private String latitude;
    @CoordinateValidation
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LONGITUDE)
    private String longitude;
    @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_MUNICIPALITY_NAME)
    private String municipalityName;
    @JsonIgnore
    private String suggestedZipCode;

    public AddressEntity(@JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_NAME_NUMBER_AND_LETTER, required = true) String address,
                         @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_STREET_ADDRESS_DESCRIPTION) String addressDescription,
                         @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESS_ENTITY_PERMANENT_STREET_ADDRESS) Boolean addressIsPermanent,
                         @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_ZIPCODE) String zipCode,
                         @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LATITUDE) String latitude,
                         @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_LONGITUDE) String longitude,
                         @JsonProperty(value = ApiConstants.PROBLEMINSTANCE_ADDRESSENTITY_MUNICIPALITY_NAME) String municipalityName) {
        this.address = address;
        this.addressDescription = addressDescription;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.municipalityName = municipalityName;
        if (addressIsPermanent != null) {
            this.addressIsPermanent = addressIsPermanent;
        } else {
            this.addressIsPermanent = true;
        }
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @JsonIgnore
    public String getLatLong() {
        return latitude + "," + longitude;
    }

    @JsonIgnore
    public String getLongLat() {
        return longitude + "," + latitude;
    }

    @JsonIgnore
    public abstract String getId();

    public String getAddress() {
        return address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setAddressIsPermanent(Boolean addressIsPermanent) {
        this.addressIsPermanent = addressIsPermanent;
    }

    public void setSuggestedZipCode(String suggestedZipCode) {
        this.suggestedZipCode = suggestedZipCode;
    }

    public String getSuggestedZipCode() {
        return suggestedZipCode;
    }

    public String getAddressDescription() {
        return addressDescription;
    }

    public Boolean getAddressIsPermanent() {
        return addressIsPermanent;
    }

    @Override
    public String toString() {
        return "Address: " + address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressEntity)) {
            return false;
        }
        AddressEntity that = (AddressEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
