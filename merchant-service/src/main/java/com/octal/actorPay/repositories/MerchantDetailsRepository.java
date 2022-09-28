package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.MerchantType;
import com.octal.actorPay.dto.MerchantResponse;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MerchantDetailsRepository extends JpaRepository<MerchantDetails, String> {



    @Query("select m from MerchantDetails m where m.user = (select u.id from User u where u.email = :email)")
    MerchantDetails findMerchantIdByEmail(@Param("email") String email);

    MerchantDetails findByIdAndDeletedFalse(String merchantId);

    MerchantDetails findByUser(User user);

    @Query("select m.businessName from MerchantDetails m where m.id = :merchantId")
    String findMerchantName(@Param("merchantId") String merchantId);

    MerchantDetails findByBusinessNameAndDeletedFalse(String merchantName);

    Long countByBusinessNameAndDeletedFalse(String merchantName);

    List<MerchantDetails> findByDeletedFalseAndIsActive(Boolean isActive, Sort sort);

    long countById(String id);

//    @Query("select new com.octal.actorPay.dto.MerchantResponse(m.id, m.user.id) from MerchantDetails m where m.user.id= :userId")
//    MerchantResponse findMerchantBasicData(@Param("userId") String userId);

    @Query("select new com.octal.actorPay.dto.MerchantResponse(m.id, m.user.id) from MerchantDetails m where m.user.email= :email")
    MerchantResponse findMerchantBasicData(@Param("email") String email);
}
