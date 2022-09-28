package com.octal.actorPay.dto.payments;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class QrCodeCreateRequest implements Serializable {

    private String type;
    private String name;
    private String usage;
    private String fixedAmount;
    private String paymentAmount;
    private String description;
    private String customer_id;
    private Long clsoeBy = 1681615838l;
    private String notes;
    @NotEmpty
    private String accountHolderName;
    @NotEmpty
    @Size(min = 11,max = 11, message = "The ifsc must be 11 characters")
    private String ifscCode;
    @NotEmpty
    private String accountNumber;
    private String userId;
    private String upiId;

//            "notes": {
//        "purpose": "Test UPI QR code notes"
//    }
}

