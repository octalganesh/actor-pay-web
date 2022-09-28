package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.MoneyTransferLimitDTO;
import com.octal.actorPay.entities.MoneyTransferLimit;

import java.util.function.Function;

public class MoneyTransferLimitTransformer {
    public static Function<MoneyTransferLimit, MoneyTransferLimitDTO> MTL_TO_DTO = (moneyTransferLimit) -> {

        MoneyTransferLimitDTO moneyTransferLimitDTO = new MoneyTransferLimitDTO();
        moneyTransferLimitDTO.setId(moneyTransferLimit.getId());
        moneyTransferLimitDTO.setCustomerAddMoney(moneyTransferLimit.getCustomerAddMoney());
        moneyTransferLimitDTO.setCustomerWithdrawMoneyToBank(moneyTransferLimit.getCustomerWithdrawMoneyToBank());
        moneyTransferLimitDTO.setCustomerTransactionLimit(moneyTransferLimit.getCustomerTransactionLimit());
        moneyTransferLimitDTO.setMerchantAddMoney(moneyTransferLimit.getMerchantAddMoney());
        moneyTransferLimitDTO.setMerchantWithdrawMoneyToBank(moneyTransferLimit.getMerchantWithdrawMoneyToBank());
        moneyTransferLimitDTO.setMerchantTransactionLimit(moneyTransferLimit.getMerchantTransactionLimit());
        moneyTransferLimitDTO.setUpdatedAt(moneyTransferLimit.getUpdatedAt());

        return moneyTransferLimitDTO;
    };
}
