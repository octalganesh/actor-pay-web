package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.QrCodeTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrCodeTransactionsRepository extends JpaRepository<QrCodeTransactions, String> {

    QrCodeTransactions findByQrTransactionId(String qrTransactionId);
}
