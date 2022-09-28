package com.octal.actorPay.dto.payments;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class QRCodeResponse implements Serializable {
    private String id;
    private Boolean fixedAmount;
    private List<String> notes;
    private String imageURL;
    private String usage;
    private String name;
    private String customerId;
    private String entity;
    private String status;
    private String type;
    private String description;
    private String accountHolderName;
    private String ifscCode;
    private String accountNumber;
}
