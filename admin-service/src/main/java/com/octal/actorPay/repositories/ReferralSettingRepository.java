package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.ReferralSetting;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleToScreenMapping;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralSettingRepository extends JpaRepository<ReferralSetting, String>, JpaSpecificationExecutor<ReferralSetting> {

    Optional<ReferralSetting> findByCreatedBy(String createdBy);

}