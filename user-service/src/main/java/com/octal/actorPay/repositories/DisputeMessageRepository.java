package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.DisputeMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DisputeMessageRepository extends
        JpaRepository<DisputeMessage,String>, JpaSpecificationExecutor<DisputeMessage> {

    List<DisputeMessage> findAllMessageByDisputeItem(Sort sort, DisputeItem disputeItem);

}
