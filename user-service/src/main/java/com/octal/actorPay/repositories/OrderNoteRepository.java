package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.OrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderNoteRepository extends JpaRepository<OrderNote,String> , JpaSpecificationExecutor<OrderNote> {

//    List<OrderNote> findByOrderId(String orderId);

    List<OrderNote> findByUserId(String userId);


}
