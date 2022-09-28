package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.MerchantReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantReportHistoryRepository extends JpaRepository<MerchantReportHistory, String>, JpaSpecificationExecutor<MerchantReportHistory> {
}
