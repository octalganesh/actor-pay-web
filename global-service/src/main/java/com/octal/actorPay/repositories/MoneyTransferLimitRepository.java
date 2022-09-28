package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.MoneyTransferLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoneyTransferLimitRepository extends JpaRepository<MoneyTransferLimit, String> {

    Optional<MoneyTransferLimit> findFirstByOrderByCreatedAtDesc();
}
