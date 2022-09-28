package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.PayrollWalletDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PayrollWalletDetailsRepository extends JpaRepository<PayrollWalletDetails, String>, JpaSpecificationExecutor<PayrollWalletDetails> {

    List<PayrollWalletDetails> findByStatusAndMerchantId(String status, String merchantId);
}
