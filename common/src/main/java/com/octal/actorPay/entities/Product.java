package com.octal.actorPay.entities;

import javax.persistence.*;

//@Entity
//@Table(name = "product")
public class Product extends AbstractPersistable{

//    @Column(name = "name")
    private String name;

//    @Column(name = "description")
    private String description;

//    @Column(name = "in_stock")
    private Integer inStock;

//    @Column(name = "unit_price")
    private Double unitPrice;

//    @Column(name = "image_url")
    private String imageUrl;

//    @OneToOne
//    @JoinColumn(name = "brand_id")
    private Brands brands;







}
