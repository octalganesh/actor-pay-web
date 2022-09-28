package com.octal.actorPay.listener.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.dto.CommonUserDTO;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.model.entities.Wallet;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

public class AddMoneyIntoWallet extends ApplicationEvent {

    private final UserDTO user;
    private final WalletTransactionResponse walletTransactionResponse;
    private final FcmUserNotificationDTO.Request fcmRequest;

    public AddMoneyIntoWallet(final UserDTO user, final WalletTransactionResponse walletTransactionResponse,final FcmUserNotificationDTO.Request fcmRequest) {
        super(user);
        this.user = user;
        this.walletTransactionResponse = walletTransactionResponse;
        this.fcmRequest = fcmRequest;
    }

    public UserDTO getUser() {
        return user;
    }

    public WalletTransactionResponse getWalletTransactionResponse() {
        return walletTransactionResponse;
    }

    public FcmUserNotificationDTO.Request getFcmRequest() {
        return fcmRequest;
    }
}
