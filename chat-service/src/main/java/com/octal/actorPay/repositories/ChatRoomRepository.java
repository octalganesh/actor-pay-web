package com.octal.actorPay.repositories;

import com.octal.actorPay.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String>, JpaSpecificationExecutor<ChatRoom> {

    ChatRoom findByOrderId(String orderId);
}
