package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Referal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferalRepository extends JpaRepository<Referal, String> {
}
