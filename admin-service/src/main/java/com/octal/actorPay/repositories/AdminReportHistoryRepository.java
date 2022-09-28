package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.AdminReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReportHistoryRepository extends JpaRepository<AdminReportHistory, String>, JpaSpecificationExecutor<AdminReportHistory> {
}
