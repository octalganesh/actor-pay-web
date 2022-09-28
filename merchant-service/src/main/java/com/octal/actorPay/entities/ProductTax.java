package com.octal.actorPay.entities;

import javax.persistence.*;

@Entity
@Table(name = "product_tax")
public class ProductTax extends AbstractPersistable{

    private double sgst;

    private double cgst;



    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

}
