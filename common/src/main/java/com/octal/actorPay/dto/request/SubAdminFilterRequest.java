package com.octal.actorPay.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class SubAdminFilterRequest {

    private String userType;
    private String name;
    private String email;
    private String contactNumber;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private Boolean isActive;
//    private Boolean phoneVerified;
//    private Boolean emailVerified;


}
