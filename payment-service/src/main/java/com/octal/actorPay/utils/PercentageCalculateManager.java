package com.octal.actorPay.utils;

import com.octal.actorPay.dto.PercentageCharges;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.WalletWithdrawRequest;
import org.springframework.stereotype.Component;

@Component
public class PercentageCalculateManager {

    public PercentageCharges calculatePercentage(WalletRequest walletAddMoneyRequest) throws Exception {
        return calculate(walletAddMoneyRequest.getCommissionPercentage(),walletAddMoneyRequest.getAmount());
    }
//    public PercentageCharges calculatePercentage(WalletRequest walletWithdrawRequest) throws Exception {
//        return calculate(walletWithdrawRequest.getCommissionPercentage(),walletWithdrawRequest.getAmount());
//    }

    private PercentageCharges calculate(double percentage, double amount) {

        double percentageAmount = (amount * percentage) / 100;
        PercentageCharges percentageCharges = new PercentageCharges();
        percentageCharges.setPercentageCharges(percentageAmount);
        percentageCharges.setBalanceAmount(amount - percentageAmount);
        percentageCharges.setOriginalAmount(amount);
        percentageCharges.setPercentage(percentage);
        return percentageCharges;
    }
}
