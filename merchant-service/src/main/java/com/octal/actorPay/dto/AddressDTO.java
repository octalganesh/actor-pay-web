package com.octal.actorPay.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AddressDTO extends BaseDTO {

    @NotBlank
    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
//    @NotBlank
    private String addressLine2;

    @Size(max = 10)
    @NotBlank
    private String zipCode;

    @Size(max = 255)
    @NotBlank
    private String city;

    @Size(max = 255)
    @NotBlank
    private String state;

    @Size(max = 100)
    @NotBlank
    private String country;

    @Size(max = 100)
    @NotBlank
    private String latitude;

    @Size(max = 100)
    @NotBlank
    private String longitude;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
