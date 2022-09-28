package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.UserOtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserOtpVerification, String> {
    Optional<UserOtpVerification> findByOtp(String otp);

    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.userId=:userId")
    Optional<UserOtpVerification> findByTypeAndUserId(@Param("type") UserOtpVerification.Types type, @Param("userId") String userId);

    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.userId=:userId")
    List<UserOtpVerification> findByTypeAndUserIdForVerification(@Param("type") UserOtpVerification.Types type, @Param("userId") String userId);

//    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.userId=:userId and uv.userVerificationStatus = :verifyStatus")
//    Optional<UserOtpVerification> findByTypeAndUserIdAndUserVerificationStatus(@Param("type") UserOtpVerification.Types type,
//                                                                               @Param("userId") String userId,
//                                                                               @Param("verifyStatus")
//                                                                                       UserOtpVerification.UserVerificationStatus userVerificationStatus);


    Optional<UserOtpVerification> findByToken(String token);

    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.userId=:userId and uv.isActive=true ")
    Optional<UserOtpVerification> findByTypeAndUserIdAndIsActiveIsTrue(@Param("type") UserOtpVerification.Types type, @Param("userId")String userId);

}