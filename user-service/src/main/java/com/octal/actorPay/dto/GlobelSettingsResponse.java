package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobelSettingsResponse {

    private Double wallet_balance;

    private Integer notification_counter;

    private UserDTO userDTO;

    private CartDTO cartDTO;




}
