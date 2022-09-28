package com.octal.actorPay.entities;

import javax.persistence.*;

@Entity
@Table(name = "order_validation")
public class OrderStatusValidation  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_status_to_be_update")
    private String OrderStatusToBeUpdate;

    @Column(name = "order_status_condition")
    private String orderStatusCondition;

    @Column(name = "user_type")
    private String userType;

    public String getOrderStatusToBeUpdate() {
        return OrderStatusToBeUpdate;
    }

    public void setOrderStatusToBeUpdate(String orderStatusToBeUpdate) {
        OrderStatusToBeUpdate = orderStatusToBeUpdate;
    }

    public String getOrderStatusCondition() {
        return orderStatusCondition;
    }

    public void setOrderStatusCondition(String orderStatusCondition) {
        this.orderStatusCondition = orderStatusCondition;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
