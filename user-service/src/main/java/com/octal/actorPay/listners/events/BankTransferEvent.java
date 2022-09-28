package com.octal.actorPay.listners.events;

import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.entities.User;
import org.springframework.context.ApplicationEvent;

public class BankTransferEvent extends ApplicationEvent {
    private final User user;
    private final WalletTransactionResponse walletTransactionResponse;
    private final FcmUserNotificationDTO.Request fcmRequest;

    public BankTransferEvent(final User user, final WalletTransactionResponse walletTransactionResponse, final FcmUserNotificationDTO.Request fcmRequest) {
        super(user);
        this.user = user;
        this.walletTransactionResponse = walletTransactionResponse;
        this.fcmRequest = fcmRequest;
    }

    public User getUser() {
        return user;
    }

    public WalletTransactionResponse getWalletTransactionResponse() {
        return walletTransactionResponse;
    }

    public FcmUserNotificationDTO.Request getFcmRequest() {
        return fcmRequest;
    }
}
