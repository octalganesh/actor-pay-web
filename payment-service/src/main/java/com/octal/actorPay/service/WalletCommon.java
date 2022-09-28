package com.octal.actorPay.service;

import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.constants.TransactionTypes;
import com.octal.actorPay.dto.PercentageCharges;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.model.entities.Wallet;
import com.octal.actorPay.model.entities.WalletTransaction;
import com.octal.actorPay.utils.PercentageCalculateManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class WalletCommon {

    private PercentageCalculateManager percentageCalculateManager;
    private MessageHelper messageHelper;
    public WalletCommon(PercentageCalculateManager percentageCalculateManager, MessageHelper messageHelper) {
        this.percentageCalculateManager=percentageCalculateManager;
        this.messageHelper=messageHelper;
    }
    public WalletTransaction setWalletTransaction(WalletRequest request, Wallet walletObj) throws Exception {
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setTransactionRemark(request.getTransactionRemark());
        walletTransaction.setUserType(request.getUserType());
        walletTransaction.setTransactionTypes(request.getTransactionTypes());
        walletTransaction.setActive(Boolean.TRUE);
        walletTransaction.setUserId(request.getUserId());
        walletTransaction.setToUser(request.getToUserId());
        walletTransaction.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        walletTransaction.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        walletTransaction.setWallet(walletObj);
        walletTransaction.setPurchaseType(request.getPurchaseType());
        walletTransaction.setTransactionAmount(request.getAmount());
        walletTransaction.setWalletTransactionId(request.getWalletTransactionId());
        walletTransaction.setParentTransaction(request.getParentTransaction());
        walletTransaction.setTransactionReason(request.getTransactionReason());
        if(request.getCommissionPercentage() != null) {
            PercentageCharges percentageCharges =
                    percentageCalculateManager.calculatePercentage(request);
            walletTransaction.setAdminCommission(percentageCharges.getPercentageCharges());
            walletTransaction.setTransferAmount(percentageCharges.getBalanceAmount());
            walletTransaction.setPercentage(percentageCharges.getPercentage());
        }else{
            walletTransaction.setAdminCommission(0d);
            walletTransaction.setTransferAmount(request.getAmount());
            walletTransaction.setPercentage(0d);
        }
        return walletTransaction;
    }
}
