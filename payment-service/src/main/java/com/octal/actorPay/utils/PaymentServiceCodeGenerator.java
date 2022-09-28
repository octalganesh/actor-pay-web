package com.octal.actorPay.utils;

import com.octal.actorPay.config.PaymentSettingsConfig;
import com.octal.actorPay.model.entities.RequestMoney;
import com.octal.actorPay.model.entities.WalletTransaction;
import com.octal.actorPay.repositories.RequestMoneyRepository;
import com.octal.actorPay.repositories.WalletTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PaymentServiceCodeGenerator {


    private WalletTransactionRepository walletTransactionRepository;
    private PaymentSettingsConfig paymentSettingsConfig;
    private RequestMoneyRepository requestMoneyRepository;

    public PaymentServiceCodeGenerator(WalletTransactionRepository walletTransactionRepository,
                                       PaymentSettingsConfig paymentSettingsConfig, RequestMoneyRepository requestMoneyRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.paymentSettingsConfig = paymentSettingsConfig;
        this.requestMoneyRepository = requestMoneyRepository;
    }

    public String getWalletTransactionCode() {
        String code = "";
        WalletTransaction walletTransaction = null;
        do {
            String newCode = getCode(paymentSettingsConfig.getWalletCode());
            walletTransaction = walletTransactionRepository.findByWalletTransactionId(newCode);
            if (walletTransaction == null) {
                code = newCode;
            }
        } while (walletTransaction != null);
        return code;
    }

    public String getWalletParentTransactionCode() {
        String code = "";
        WalletTransaction walletTransaction = null;
        do {
            String newCode = getCode(paymentSettingsConfig.getParentWalletCode());
            walletTransaction = walletTransactionRepository.findByParentTransaction(newCode);
            if (walletTransaction == null) {
                code = newCode;
            }
        } while (walletTransaction != null);
        return code;
    }

    private String getCode(String prefix) {
        Random r = new Random(System.currentTimeMillis());
        int id = ((1 + r.nextInt(2)) * 10000000 + r.nextInt(10000000));
        String codePrefix = prefix;
        return codePrefix + id;
    }

    public String getRequestMoneyCode() {
        String code = "";
        RequestMoney requestMoney = null;
        do {
            String newCode = getCode(paymentSettingsConfig.getRequestMoneyCode());
            requestMoney = requestMoneyRepository.findByRequestCode(newCode);
            if (requestMoney == null) {
                code = newCode;
            }
        } while (requestMoney != null);
        return code;
    }
}
