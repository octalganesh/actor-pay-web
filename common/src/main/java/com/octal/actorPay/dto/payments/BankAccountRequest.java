package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountRequest implements Serializable {


    private String name;

    private String email;

    private String contact;

    private String accountType;
    @NotEmpty
    private String accountHolderName;

    private String userId;

    @NotEmpty
    @Size(min = 11,max = 11, message = "The ifsc must be 11 characters")
    private String ifscCode;

    @NotEmpty
    private String accountNumber;

    @JsonIgnore
    private Map<String,String> bank_account;

    private String userType;

    private String contactId;

    @JsonProperty("self")
    private boolean self;

    @JsonProperty("primaryAccount")
    private boolean primaryAccount;
}
