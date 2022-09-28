package com.octal.actorPay.service;

import com.octal.actorPay.dto.MoneyTransferLimitDTO;
import org.springframework.stereotype.Service;

@Service
public interface MoneyTransferLimitService {

    MoneyTransferLimitDTO getMtlData();

    void createOrUpdateMtl(MoneyTransferLimitDTO moneyTransferLimitDTO);
}
