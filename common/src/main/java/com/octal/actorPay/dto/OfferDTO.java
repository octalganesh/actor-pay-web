package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.octal.actorPay.constants.OfferType;
import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferDTO {

    private String offerId;

    @Size(max = 150)
    @NotBlank
    private String offerTitle;

    @Size(max = 255)
    @NotBlank
    private String offerDescription;

    @Min(value = 0)
    private Float offerInPercentage = 0.0f;

    @Min(value = 0)
    private Float maxDiscount = 0.0f;

    @Min(value = 0)
    private Float minOfferPrice = 0.0f;

    @Min(value = 0)
    @NotNull
    private Float offerInPrice = 0.0f;

    private String offerCode;

    private String categoryId;

    @NotBlank
    private String offerType;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime offerStartDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime offerEndDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    @NotNull
    @Min(value = 1)
    private Integer numberOfUsage;

    private Integer ordersPerDay;

    private String userId;

    private String userName;

    private Boolean isActive;

    private String visibilityLevel;

    private String image;

    private String status;

    @Min(value = 0)
    private Float useLimit = 0.0f;

    @Min(value = 0)
    private Float singleUserLimit = 0.0f;

}


