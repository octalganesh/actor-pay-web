package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.ReferralHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralHistoryRepository extends JpaRepository<ReferralHistory, String>, JpaSpecificationExecutor<ReferralHistory> {

}