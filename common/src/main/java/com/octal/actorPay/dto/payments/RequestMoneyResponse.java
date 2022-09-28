package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.constants.RequestMoneyStatus;
import com.octal.actorPay.dto.BaseDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestMoneyResponse {

    private WalletTransactionResponse walletTransactionResponse;

    private RequestMoneyDTO requestMoneyDTO;

}
