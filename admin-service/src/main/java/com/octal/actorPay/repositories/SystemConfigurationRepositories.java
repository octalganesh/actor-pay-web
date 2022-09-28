package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.SubCategories;
import com.octal.actorPay.entities.SystemConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigurationRepositories extends JpaRepository<SystemConfiguration, String> {


    Optional<SystemConfiguration> findSystemConfigurationByParamName(String key);
    List<SystemConfiguration> findSystemConfigurationByParamNameIn(List<String> keys);


}