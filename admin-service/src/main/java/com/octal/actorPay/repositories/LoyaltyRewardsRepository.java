package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.Event;
import com.octal.actorPay.entities.LoyaltyRewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyRewardsRepository extends JpaRepository<LoyaltyRewards, String>, JpaSpecificationExecutor<LoyaltyRewards> {

    LoyaltyRewards findByEvent(Event event);
}
