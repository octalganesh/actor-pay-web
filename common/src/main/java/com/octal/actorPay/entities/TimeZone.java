package com.octal.actorPay.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


//@Entity
//@Table(name = "time_zones")
public class TimeZone extends AbstractPersistable {


//    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "zone", nullable = false)
    private String zone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        return name;
    }
}
