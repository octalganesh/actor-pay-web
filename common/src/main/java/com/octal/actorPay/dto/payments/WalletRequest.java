package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.constants.TransactionTypes;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletRequest implements Serializable {

    private String currentUserId;

    @NotNull
    @Min(value = 1)
    private Double amount; // same as Transaction amount

    private String transactionRemark;

    private String transactionReason;

//    @NotEmpty
    private String beneficiaryUserType;

    private String userType;

    private Double commissionPercentage;

    private String walletTransactionId;

    private String parentTransaction;

    private String beneficiaryId;

    private String userIdentity;

    private TransactionTypes transactionTypes;

    private PurchaseType purchaseType;

    private Double transferAmount;

    private String toUserId;

    private String userId;

    private String orderNo;

    private String adminUserId;

    private String identityType;

    private String transferMode;

    private PaymentGatewayResponse gatewayResponse;

    private String paymentMethod;

}
