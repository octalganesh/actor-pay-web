package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.LoyaltyRewardsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyRewardsUserRepository extends JpaRepository<LoyaltyRewardsUser, String> {
    Optional<LoyaltyRewardsUser> findByUserId(String userId);
}
