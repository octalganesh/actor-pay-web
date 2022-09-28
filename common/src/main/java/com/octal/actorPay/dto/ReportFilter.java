package com.octal.actorPay.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ReportFilter {

    private String userId;
    private String merchantId;
    private String type;
    private String durationType = "";
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private String name;
    private String email;
    private String contact;
    private String reportName;
    private String adminId;
}
