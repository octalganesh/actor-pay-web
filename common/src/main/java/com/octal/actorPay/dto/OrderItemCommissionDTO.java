package com.octal.actorPay.dto;

public class OrderItemCommissionDTO {


    private double cancellationFee;

    private double returnFee;

    private double adminCommission;

    private double returnDays;

    private OrderItemDTO orderItemDTO;

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public double getReturnFee() {
        return returnFee;
    }

    public void setReturnFee(double returnFee) {
        this.returnFee = returnFee;
    }

    public double getAdminCommission() {
        return adminCommission;
    }

    public void setAdminCommission(double adminCommission) {
        this.adminCommission = adminCommission;
    }

    public double getReturnDays() {
        return returnDays;
    }

    public void setReturnDays(double returnDays) {
        this.returnDays = returnDays;
    }

    public OrderItemDTO getOrderItemDTO() {
        return orderItemDTO;
    }

    public void setOrderItemDTO(OrderItemDTO orderItemDTO) {
        this.orderItemDTO = orderItemDTO;
    }
}
