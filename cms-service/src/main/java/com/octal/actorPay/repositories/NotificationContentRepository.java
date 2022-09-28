package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.EmailTemplate;
import com.octal.actorPay.entities.NotificationContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationContentRepository extends JpaRepository<NotificationContent, String> , JpaSpecificationExecutor<NotificationContent> {
    Optional<NotificationContent> findById(String id);
    Page<NotificationContent> findByTitleContainingIgnoreCase(String keyword, Pageable pageRequest);
    Optional<NotificationContent> findBySlug(String slug);
}