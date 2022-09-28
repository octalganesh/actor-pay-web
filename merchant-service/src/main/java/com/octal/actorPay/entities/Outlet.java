package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "outlet")
@Entity
public class Outlet extends AbstractPersistable {

    @Column(name = "title")
    private String title;

    @Column(name = "licence_number")
    private String licenceNumber;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "extension_number")
    private String extensionNumber;

    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private MerchantDetails merchantDetails;
}
