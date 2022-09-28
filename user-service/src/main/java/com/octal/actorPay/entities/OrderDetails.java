package com.octal.actorPay.entities;


import org.hibernate.criterion.Order;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "order_details")
public class OrderDetails extends AbstractPersistable{

    @Column(name = "order_no",nullable = false,unique = true)
    private String orderNo;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "total_price_after_promo")
    private double totalPriceAfterPromo = 0;

    @Column(name = "total_sgst")
    private double totalSgst;

    @Column(name = "total_cgst")
    private double totalCgst;

    @Column(name = "total_taxable_value")
    private double TotalTaxableValue;

    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    @Column(name = "order_status")
    private String orderStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User customer;

    @Column(name = "merchant_id",nullable = false)
    private String merchantId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_address_id")
    private OrderAddress orderAddress;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "order_receipt")
    String orderReceipt;

    public String getOrderReceipt() {
        return orderReceipt;
    }

    public void setOrderReceipt(String orderReceipt) {
        this.orderReceipt = orderReceipt;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(OrderAddress orderAddress) {
        this.orderAddress = orderAddress;
    }
//    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<OrderStatusHistory> orderStatusHistories;

    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderNote> orderNoteList;

    public List<OrderNote> getOrderNoteList() {
        return orderNoteList;
    }

    public void setOrderNoteList(List<OrderNote> orderNoteList) {
        this.orderNoteList = orderNoteList;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPriceAfterPromo() {
        return totalPriceAfterPromo;
    }

    public void setTotalPriceAfterPromo(double totalPriceAfterPromo) {
        this.totalPriceAfterPromo = totalPriceAfterPromo;
    }

    public double getTotalSgst() {
        return totalSgst;
    }

    public void setTotalSgst(double totalSgst) {
        this.totalSgst = totalSgst;
    }

    public double getTotalCgst() {
        return totalCgst;
    }

    public void setTotalCgst(double totalCgst) {
        this.totalCgst = totalCgst;
    }

    public double getTotalTaxableValue() {
        return TotalTaxableValue;
    }

    public void setTotalTaxableValue(double totalTaxableValue) {
        TotalTaxableValue = totalTaxableValue;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
