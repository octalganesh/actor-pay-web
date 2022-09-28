package com.octal.actorPay.entities;

import javax.persistence.*;

//@Entity
//@Table(name = "vendor")
public class Vendor extends AbstractPersistable {

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "licence_number", nullable = false)
    private String licenceNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
