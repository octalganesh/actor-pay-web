package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDeviceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeviceDetailsRepository extends JpaRepository<UserDeviceDetails, String> {

    UserDeviceDetails findByUserId(String userId);

}