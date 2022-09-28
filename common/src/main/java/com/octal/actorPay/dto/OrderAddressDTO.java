package com.octal.actorPay.dto;

import com.octal.actorPay.entities.CommonAddressFields;
import com.octal.actorPay.entities.User;

import javax.persistence.*;


public class OrderAddressDTO extends CommonAddressFields {

    @Column(name = "name")
    private String name;

    @Column(name = "extension_number")
    private String extensionNumber;

    @Column(name = "primary_contact_number")
    private String primaryContactNumber;

    @Column(name = "secondary_contact_nusmber")
    private String secondaryContactNumber;

    @Column(name = "is_primary")
    private Boolean primary;

    @Column(name = "address_title")
    private String addressTitle;

    @Column(name = "area")
    private String area;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String profileAddressId;

    public String getProfileAddressId() {
        return profileAddressId;
    }

    public void setProfileAddressId(String profileAddressId) {
        this.profileAddressId = profileAddressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    public void setPrimaryContactNumber(String primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    public String getSecondaryContactNumber() {
        return secondaryContactNumber;
    }

    public void setSecondaryContactNumber(String secondaryContactNumber) {
        this.secondaryContactNumber = secondaryContactNumber;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
