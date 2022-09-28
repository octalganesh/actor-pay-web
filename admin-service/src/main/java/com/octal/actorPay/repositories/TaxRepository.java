package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.Tax;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TaxRepository extends JpaRepository<Tax, String>, JpaSpecificationExecutor<Tax> {

    Page<Tax> findByIsActive(Pageable pageable,Boolean isActive);
    List<Tax> findByIsActive(Boolean isActive, Sort sort);
    List<Tax> findByIsActive(Boolean isActive);
//    Optional<Tax> findById(String taxId);
    Tax findByHsnCode(String hsnCode);
}
