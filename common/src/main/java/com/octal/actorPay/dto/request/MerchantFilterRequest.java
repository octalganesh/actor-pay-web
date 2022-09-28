package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDate;

@Data
public class MerchantFilterRequest {

    private String userType;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String resourceType;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private Boolean active;
    private Boolean phoneVerified;
    private Boolean emailVerified;

}
