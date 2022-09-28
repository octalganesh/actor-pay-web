package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.constants.TransactionTypes;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletShoppingRequest extends WalletRequest {

   private String paymentMethod;
}
