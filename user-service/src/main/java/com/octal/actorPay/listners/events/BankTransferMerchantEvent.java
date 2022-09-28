package com.octal.actorPay.listners.events;

import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.MerchantDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.entities.User;
import org.springframework.context.ApplicationEvent;

public class BankTransferMerchantEvent extends ApplicationEvent {
    private final MerchantDTO user;
    private final WalletTransactionResponse walletTransactionResponse;
    private final FcmUserNotificationDTO.Request fcmRequest;

    public BankTransferMerchantEvent(final MerchantDTO user, final WalletTransactionResponse walletTransactionResponse, final FcmUserNotificationDTO.Request fcmRequest) {
        super(user);
        this.user = user;
        this.walletTransactionResponse = walletTransactionResponse;
        this.fcmRequest = fcmRequest;
    }

    public MerchantDTO getUser() {
        return user;
    }

    public WalletTransactionResponse getWalletTransactionResponse() {
        return walletTransactionResponse;
    }

    public FcmUserNotificationDTO.Request getFcmRequest() {
        return fcmRequest;
    }
}
