package com.octal.actorPay.entities;

import javax.persistence.*;

//@Entity
//@Table(name = "user_details")
public class UserDetails extends AbstractPersistable {

//    @ManyToOne
//    @JoinColumn(name = "user_id")
    private User user;

//    @OneToOne
//    @JoinColumn(name = "address_id")
//    private Address address;

//    @OneToOne
//    @JoinColumn(name = "contact_id")
    private Contacts contacts;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    public Address getAddress() {
//        return address;
//    }
//
//    public void setAddress(Address address) {
//        this.address = address;
//    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }
}
