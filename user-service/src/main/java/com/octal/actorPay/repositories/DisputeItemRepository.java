package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DisputeItemRepository extends JpaRepository<DisputeItem ,String> , JpaSpecificationExecutor<DisputeItem> {

    Optional<DisputeItem> findByIdAndDeletedFalse(String disputeId);

    Optional<DisputeItem> findByIdAndDeletedFalseAndUserId(String disputeId,String userId);

    Optional<DisputeItem> findByIdAndDeletedFalseAndMerchantId(String disputeId,String merchantId);

    DisputeItem findByDisputeCodeAndDeletedFalse(String disputeCode);
}
