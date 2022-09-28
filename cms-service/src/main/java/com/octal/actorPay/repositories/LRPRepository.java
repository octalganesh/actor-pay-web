package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.LRP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LRPRepository extends JpaRepository<LRP, String> {

}