package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.QrCodeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QrCodeDetailsRepository extends JpaRepository<QrCodeDetails, String> {

    QrCodeDetails findByQrCodeId(String qrCodeId);

    QrCodeDetails findByUserIdAndIfscAndAccountNumber(String userId, String ifsc, String accountNumber);

    List<QrCodeDetails> findByFundAccountIdIn(List<String> fundAccountIds);
}
