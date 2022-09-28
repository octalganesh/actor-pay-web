package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.ShippingAddress;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<UserSetting, String> {

    Page<UserSetting> findUserSettingByUserId(String userId, Pageable pageRequest);
    Optional<UserSetting> findByUser(User user);
    UserSetting findByIdAndDeletedFalse(String id);

}
