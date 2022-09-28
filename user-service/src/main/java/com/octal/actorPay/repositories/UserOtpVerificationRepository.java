package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.UserOtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpVerificationRepository extends JpaRepository<UserOtpVerification, String> {
    Optional<UserOtpVerification> findByOtp(String otp);
    Optional<UserOtpVerification> findByOtpAndIsActiveIsTrue(String otp);

    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.userId=:userId")
    Optional<UserOtpVerification> findByTypeAndUserId(@Param("type") UserOtpVerification.Types type, @Param("userId")String userId);

    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.userId=:userId and uv.isActive=true ")
    Optional<UserOtpVerification> findByTypeAndUserIdAndIsActiveIsTrue(@Param("type") UserOtpVerification.Types type, @Param("userId")String userId);


    Optional<UserOtpVerification> findByToken(String token);
}