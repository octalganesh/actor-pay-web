package com.octal.actorPay.dto;

import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.entities.User;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class EkycFilterRequest {

    private String docType;

    private String documentData;

    private User user;

    private EkycStatus ekycStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

}
