package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.LoyaltyRewardsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyRewardsHistoryRepository extends JpaRepository<LoyaltyRewardsHistory, String>, JpaSpecificationExecutor<LoyaltyRewardsHistory> {

    List<LoyaltyRewardsHistory> findByEventAndUserId(String event, String userId);
}
