package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderReportResponse {
    private String orderId;

    private String orderNo;

    private int totalQuantity;

    private double totalPrice;

    private double totalSgst;

    private double totalCgst;

    private double TotalTaxableValue;

    private UserReportDTO customer;

    private String  merchantId;

    private String orderStatus;

    @JsonIgnore
    private List<OrderItemDTO> orderItemDtos;

    private Object cancelOrderDTO;

    private MerchantDTO merchantDTO;

    private List<OrderNoteReport> orderNotesDtos;

    private String orderNoteDescription;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @JsonIgnore
    private OrderAddressDTO shippingAddressDTO;

    private String paymentMethod;

    private ApplyPromoCodeResponse promoCodeResponse;
}
