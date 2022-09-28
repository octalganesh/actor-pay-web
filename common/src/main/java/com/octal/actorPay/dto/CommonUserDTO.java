package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonUserDTO implements Serializable {

    private String userId;

    private String merchantId;

    private String businessName;

    private String firstName;

    private String lastName;

    private String email;

    private String contactNumber;

    private String extension;

    private String userType;

}
