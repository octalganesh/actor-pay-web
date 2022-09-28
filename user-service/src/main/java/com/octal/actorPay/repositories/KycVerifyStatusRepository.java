package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.KycVerifyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycVerifyStatusRepository extends JpaRepository<KycVerifyStatus,String> {

       Optional<KycVerifyStatus> findByUserId(String userId);


}
