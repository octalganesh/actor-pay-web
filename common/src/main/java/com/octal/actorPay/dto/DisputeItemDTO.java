package com.octal.actorPay.dto;

import javax.validation.constraints.NotBlank;

import java.util.List;

public class DisputeItemDTO extends BaseDTO {

    private String disputeId;

    private String disputeCode;

    private String OrderItemId;

    private String UserId;

    private String MerchantId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String imagePath;

    private MerchantDTO merchantDTO;

    private UserDTO userDTO;

    private OrderDetailsDTO orderDetailsDTO;

    private ProductDTO productDTO;

    private Double penalityPercentage;

    private String status;

    private boolean disputeFlag;

    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    private List<DisputeMessageDTO> disputeMessages;

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getOrderItemId() {
        return OrderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        OrderItemId = orderItemId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public MerchantDTO getMerchantDTO() {
        return merchantDTO;
    }

    public void setMerchantDTO(MerchantDTO merchantDTO) {
        this.merchantDTO = merchantDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public OrderDetailsDTO getOrderDetailsDTO() {
        return orderDetailsDTO;
    }

    public void setOrderDetailsDTO(OrderDetailsDTO orderDetailsDTO) {
        this.orderDetailsDTO = orderDetailsDTO;
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    public List<DisputeMessageDTO> getDisputeMessages() {
        return disputeMessages;
    }

    public void setDisputeMessages(List<DisputeMessageDTO> disputeMessages) {
        this.disputeMessages = disputeMessages;
    }

    public boolean isDisputeFlag() {
        return disputeFlag;
    }

    public void setDisputeFlag(boolean disputeFlag) {
        this.disputeFlag = disputeFlag;
    }

    public String getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(String disputeId) {
        this.disputeId = disputeId;
    }

    public String getDisputeCode() {
        return disputeCode;
    }

    public void setDisputeCode(String disputeCode) {
        this.disputeCode = disputeCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }



    public Double getPenalityPercentage() {
        return penalityPercentage;
    }

    public void setPenalityPercentage(Double penalityPercentage) {
        this.penalityPercentage = penalityPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
