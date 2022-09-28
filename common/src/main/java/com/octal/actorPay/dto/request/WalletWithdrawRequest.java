package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletWithdrawRequest implements Serializable {

    @NotNull
    @Min(value = 1)
    private Double withdrawAmount;

    private String currentUserId;

    private Double commissionPercentage;

    private String userType;
}
