package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.BankTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface BankTransactionsRepository extends JpaRepository<BankTransactions, String>, JpaSpecificationExecutor<BankTransactions> {

    BankTransactions findByOrderId(String orderId);

    List<BankTransactions> findByCreatedAtBetweenAndUserId(LocalDateTime startDate, LocalDateTime endDate, String userId);

    BankTransactions findByBankTransactionId(String transactionId);
}
