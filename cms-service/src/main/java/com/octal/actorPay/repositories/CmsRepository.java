package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Cms;
import com.octal.actorPay.entities.EmailTemplate;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.specification.SpecificationFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CmsRepository extends JpaRepository<Cms, String>, JpaSpecificationExecutor<Cms> {
    Page<Cms> findByCmsType(Integer emailTemplates, Pageable pageRequest);
    Optional<Cms> findByIdAndCmsType(String id, int cmsType);
    Page<Cms> findByTitleContainingIgnoreCase(String keyword, Pageable pageRequest);
    Page<Cms> findByCmsTypeAndTitleContainingIgnoreCase(Integer emailTemplates, String searchByTitle, Pageable pageRequest);
    Optional<Cms> findByCmsType(Integer cmsType);

}