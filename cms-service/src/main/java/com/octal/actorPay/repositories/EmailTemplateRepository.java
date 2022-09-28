package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, String> , JpaSpecificationExecutor<EmailTemplate> {
    Optional<EmailTemplate> findById(String id);
    Page<EmailTemplate> findByTitleContainingIgnoreCase(String keyword, Pageable pageRequest);
    Optional<EmailTemplate> findBySlug(String slug);
}