package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

//@Data
@Entity
@Table(name = "contactUs")
public class ContactUs extends AbstractPersistable {
    @Column(name = "name")
    private String name;

    @Column(name = "mail")
    private String mail;

    @Lob
    @Column(name = "text")
    private String text;

    @Column(name = "type")
    private String type;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
