package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.octal.actorPay.constants.RequestMoneyStatus;
import com.octal.actorPay.dto.BaseDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestMoneyDTO extends BaseDTO {

    private String requestId;

    private String userId;

    private String userName;

    private String userEmail;

    @NotEmpty
    private String userIdentity;

    private String toUserId;

    private String toUserEmail;

    private String toUserName;

    @NotNull
    @Min(value = 1)
    private Double amount;

    private RequestMoneyStatus requestMoneyStatus;

    private String requestUserTypeFrom;

    @NotEmpty
    private String requestUserTypeTo;

    @JsonIgnore
    private boolean myRequest;

    private boolean accepted;

    private String transferMode;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiryDate;

    private boolean isExpired;

    @NotEmpty
    private String reason;


}
