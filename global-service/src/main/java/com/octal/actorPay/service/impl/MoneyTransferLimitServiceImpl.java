package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.MoneyTransferLimitDTO;
import com.octal.actorPay.entities.MoneyTransferLimit;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.MoneyTransferLimitRepository;
import com.octal.actorPay.service.MoneyTransferLimitService;
import com.octal.actorPay.transformer.MoneyTransferLimitTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class MoneyTransferLimitServiceImpl implements MoneyTransferLimitService {


    @Autowired
    private MoneyTransferLimitRepository moneyTransferLimitRepository;

    /***************** MoneyTransferLimit **************/

    @Override
    public MoneyTransferLimitDTO getMtlData() {

        Optional<MoneyTransferLimit> firstByCreatedAt = moneyTransferLimitRepository.findFirstByOrderByCreatedAtDesc();
        if (firstByCreatedAt.isPresent()) {
            return firstByCreatedAt.map(moneyTransferLimitObj -> MoneyTransferLimitTransformer.MTL_TO_DTO.apply(moneyTransferLimitObj)).orElse(null);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrUpdateMtl(MoneyTransferLimitDTO moneyTransferLimitDTO) {

        List<MoneyTransferLimit> moneyTransferLimitList = moneyTransferLimitRepository.findAll();
        if(moneyTransferLimitList.isEmpty()){
            MoneyTransferLimit moneyTransferLimit = new MoneyTransferLimit();

            moneyTransferLimit.setId(moneyTransferLimitDTO.getId());
            moneyTransferLimit.setCustomerAddMoney(moneyTransferLimitDTO.getCustomerAddMoney()) ;
            moneyTransferLimit.setCustomerWithdrawMoneyToBank(moneyTransferLimitDTO.getCustomerWithdrawMoneyToBank());
            moneyTransferLimit.setCustomerTransactionLimit(moneyTransferLimitDTO.getCustomerTransactionLimit());
            moneyTransferLimit.setMerchantAddMoney(moneyTransferLimitDTO.getMerchantAddMoney());
            moneyTransferLimit.setMerchantWithdrawMoneyToBank(moneyTransferLimitDTO.getMerchantWithdrawMoneyToBank());
            moneyTransferLimit.setMerchantTransactionLimit(moneyTransferLimitDTO.getMerchantTransactionLimit());
            moneyTransferLimit.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            moneyTransferLimitRepository.save(moneyTransferLimit);
        } else {
            Optional<MoneyTransferLimit> mtl = moneyTransferLimitRepository.findById(moneyTransferLimitDTO.getId());
            if (mtl.isPresent()) {
                mtl.get().setCustomerAddMoney(moneyTransferLimitDTO.getCustomerAddMoney());
                mtl.get().setCustomerWithdrawMoneyToBank(moneyTransferLimitDTO.getCustomerWithdrawMoneyToBank());
                mtl.get().setCustomerTransactionLimit(moneyTransferLimitDTO.getCustomerTransactionLimit());
                mtl.get().setMerchantAddMoney(moneyTransferLimitDTO.getMerchantAddMoney());
                mtl.get().setMerchantWithdrawMoneyToBank(moneyTransferLimitDTO.getMerchantWithdrawMoneyToBank());
                mtl.get().setMerchantTransactionLimit(moneyTransferLimitDTO.getMerchantTransactionLimit());
                mtl.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                moneyTransferLimitRepository.save(mtl.get());
            } else {
                throw new ObjectNotFoundException("MoneyTransferLimit not found for the given id: "+ moneyTransferLimitDTO.getId());
            }
        }
    }

}
