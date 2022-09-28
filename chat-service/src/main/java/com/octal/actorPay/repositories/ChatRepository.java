package com.octal.actorPay.repositories;

import com.octal.actorPay.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String>, JpaSpecificationExecutor<Chat> {
}
