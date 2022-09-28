package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @Query(value = "select * from users u where contact_number = :userIdentity  or  email = :userIdentity  " +
            "or upi_qr_code = :userIdentity or id = :userIdentity ", nativeQuery = true)
    Optional<User> findUserByEmailOrContactNumber(@Param("userIdentity") String userIdentity);

    Optional<User> findUserByContactNumber(@Param("contactNumber") String contactNumber);

    Optional<User> findByIdAndDeletedFalse(String id);

    @Query(value = "select * from users where id in (select user_id from merchant_details where id in (\n" +
            "select merchant_id from merchant_submerchant where submerchant_id = :subMerchantId))",nativeQuery = true)
    Optional<User> findByPrimaryMerchantBySubMerchantId(@Param("subMerchantId") String subMerchantId);

    @Query(value = "select count(*) from User u where u.email != :email and u.aadharNumber = :aadharNumber" +
            " and u.ekycStatus != :ekycStatus")
    Long findAadhaarCount(@Param("email") String email,@Param("aadharNumber") String aadharNumber,
                          @Param("ekycStatus") EkycStatus ekycStatus);

    @Query(value = "select count(*) from User u where u.email != :email and u.panNumber = :panNumber " +
            "and u.ekycStatus != :ekycStatus")
    Long findPanCount(@Param("email") String email,@Param("panNumber") String panNumber,
                      @Param("ekycStatus")EkycStatus ekycStatus);

    @Query(value = "select count(*) from User u where u.aadharNumber = :aadharNumber")
    Long findByAadhaarNumberCount(@Param("aadharNumber") String aadharNumber);

    @Query(value = "select count(*) from User u where u.panNumber = :panNumber")
    Long findByPanNumberCount(@Param("panNumber") String panNumber);

}