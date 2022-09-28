package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletDTO extends BaseDTO {

    private String userId;

    @JsonProperty("amount")
    private Double balanceAmount = 0d;

    private String userType;

}