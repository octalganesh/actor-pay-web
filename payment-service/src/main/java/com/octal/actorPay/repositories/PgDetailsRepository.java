package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.PgDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PgDetailsRepository extends JpaRepository<PgDetails,String> {

    Optional<PgDetails> findByPaymentTypeId(String paymentTypeId);
}
