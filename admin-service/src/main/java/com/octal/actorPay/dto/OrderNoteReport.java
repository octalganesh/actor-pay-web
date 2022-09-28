package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OrderNoteReport {

    private String orderNoteId;

    private String orderId;

    private String orderNo;

    private String orderNoteBy;

    private String userId;

    private String merchantId;

    private String userType;

    private String orderNoteDescription;

    @JsonProperty("active")
    private Boolean active;

    @JsonIgnore
    private List<String> orderItemIds;

    @JsonIgnore
    private String cancellationReason;

    @JsonIgnore
    private String image;

    @JsonIgnore
    private String noteFlowType;

    @JsonIgnore
    private String productName;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
}

