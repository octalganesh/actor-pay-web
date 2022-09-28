package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.model.entities.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String>, JpaSpecificationExecutor<WalletTransaction> {

    Page<WalletTransaction> findWalletTransactionByUserId(String userId, Pageable pageable);

    WalletTransaction findByWalletTransactionId(String transactionId);
    WalletTransaction findByParentTransaction(String parentTransactionId);

    long countByPurchaseType(PurchaseType purchaseType);
}
