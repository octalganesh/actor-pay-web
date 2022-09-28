package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.SystemUserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemUserNotificationRepository extends JpaRepository<SystemUserNotification, String> {

    Page<SystemUserNotification> findBySystemUserId(String systemUserId, Pageable pageable);


}
