package com.octal.actorPay.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

@MappedSuperclass
public class CommonAddressFields extends AbstractPersistable {


    @Column(name = "addressLine1")
    @Size(max = 255)
    private String addressLine1;

    @Column(name = "addressLine2")
    @Size(max = 255)
    private String addressLine2;

    @Column(name = "zipCode")
    @Size(max = 10)
    private String zipCode;

    @Column(name = "city")
    @Size(max = 255)
    private String city;

    @Column(name = "state")
    @Size(max = 100)
    private String state;

    @Column(name = "country")
    @Size(max = 100)
    private String country;

    @Column(name = "latitude")
    @Size(max = 100)
    private String latitude;

    @Column(name = "longitude")
    @Size(max = 100)
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

