package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailsDTO {

    private String orderId;

    private String orderNo;

    private int totalQuantity;

    private double totalPrice;

    private double totalSgst;

    private double totalCgst;

    private double TotalTaxableValue;

    private UserDTO customer;

    private String  merchantId;
//
//    private String merchantName;

    private String orderStatus;

    private List<OrderItemDTO> orderItemDtos;

    private CancelOrderDTO cancelOrderDTO;

    private MerchantDTO merchantDTO;

    private List<OrderNoteDTO> orderNoteList;

    private String orderNoteDescription;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private OrderAddressDTO shippingAddressDTO;;;

    private String paymentMethod;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderAddressDTO getShippingAddressDTO() {
        return shippingAddressDTO;
    }

    public void setShippingAddressDTO(OrderAddressDTO shippingAddressDTO) {
        this.shippingAddressDTO = shippingAddressDTO;
    }

    public List<OrderNoteDTO> getOrderNoteList() {
        return orderNoteList;
    }

    public void setOrderNoteList(List<OrderNoteDTO> orderNoteList) {
        this.orderNoteList = orderNoteList;
    }

    public String getOrderNoteDescription() {
        return orderNoteDescription;
    }

    public void setOrderNoteDescription(String orderNoteDescription) {
        this.orderNoteDescription = orderNoteDescription;
    }
    private List<OrderNoteDTO> orderNotesDtos;

    public List<OrderNoteDTO> getOrderNotesDtos() {
        return orderNotesDtos;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setOrderNotesDtos(List<OrderNoteDTO> orderNotesDtos) {
        this.orderNotesDtos = orderNotesDtos;
    }

    public MerchantDTO getMerchantDTO() {
        return merchantDTO;
    }

    public void setMerchantDTO(MerchantDTO merchantDTO) {
        this.merchantDTO = merchantDTO;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CancelOrderDTO getCancelOrderDTO() {
        return cancelOrderDTO;
    }

    public void setCancelOrderDTO(CancelOrderDTO cancelOrderDTO) {
        this.cancelOrderDTO = cancelOrderDTO;
    }

    public List<OrderItemDTO> getOrderItemDtos() {
        return orderItemDtos;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOrderItemDtos(List<OrderItemDTO> orderItemDtos) {
        this.orderItemDtos = orderItemDtos;
    }

//    public String getMerchantId() {
//        return merchantId;
//    }
//
//    public void setMerchantId(String merchantId) {
//        this.merchantId = merchantId;
//    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public UserDTO getCustomer() {
        return customer;
    }

    public void setCustomer(UserDTO customer) {
        this.customer = customer;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
