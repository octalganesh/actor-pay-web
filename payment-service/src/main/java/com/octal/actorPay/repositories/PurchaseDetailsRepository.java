package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.PurchaseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseDetailsRepository extends JpaRepository<PurchaseDetails,String> {
}
