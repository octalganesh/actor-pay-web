package com.octal.actorPay.entities;


import javax.persistence.*;

//@Entity
//@Table(name = "order_type")
public class OrderType extends AbstractPersistable {

//    @Column(name = "type", length = 30)
    private String type;

//    @Column(name = "description")
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
