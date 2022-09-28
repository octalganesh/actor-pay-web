package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, String> {
    Page<FAQ> findByQuestionContainingIgnoreCase(String searchByTitle, Pageable pageRequest);
}