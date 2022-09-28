package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PayrollWalletDTO  implements Serializable {
    double totalAmount;
    Integer totalUser;
    double merchantBalance;
    List<PayrollDTO> payrollWalletDetails;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PayrollDTO  implements Serializable {
        String id;
        double amount;
        String userId;
        String merchantId;
        String status;
        String transactionId;
        String receiverEmail;
    }
}
