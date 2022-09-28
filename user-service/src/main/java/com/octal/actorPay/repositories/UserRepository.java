package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.enums.SocialLoginTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {

    User findByIdAndIsActiveTrueAndDeletedFalse(String id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findBySocialKey(String socialKey);

    Optional<User> findByReferralCode(String referralCode);
    Optional<User> findByEmailAndIsActiveTrueAndDeletedFalse(String email);
    Optional<User> findUserByContactNumber(@Param("contactNumber") String contactNumber);

   /* @Query(value = "select * from users u where u.contactNumber = :contactNumber ", nativeQuery = true)
    Optional<User> findByContactNumber(@Param("contactNumber")  String contactNumber);*/

    Optional<User> findUserByEmailOrContactNumber(String email, String contactNumber);

    @Query(value = "select * from users u where contact_number = :userIdentity  or  email = :userIdentity or " +
            "upi_qr_code = :userIdentity or id = :userIdentity", nativeQuery = true)
    User getUserDetailsByEmilOrContactNO(@Param("userIdentity") String userIdentity);

    @Query(value = "select count(*) from User u where u.email != :email and u.aadharNumber = :aadharNumber")
    Long findAadhaarDuplicateCount(@Param("email") String email, @Param("aadharNumber") String aadharNumber);

    @Query(value = "select count(*) from User u where u.email != :email and u.panNumber = :panNumber")
    Long findPanDuplicateCount(@Param("email") String email,@Param("panNumber") String panNumber);

    @Query(value = "select count(*) from User u where u.email != :email and" +
            " u.firstName = :firstName and u.lastName = :lastName")
    Long findDuplicateName(@Param("email") String email,@Param("firstName") String firstName,
                           @Param("lastName") String lastName);



    @Query(value = "select count(*) from User u where u.aadharNumber = :aadharNumber")
    Long findByAadhaarNumberCount(@Param("aadharNumber") String aadharNumber);

    @Query(value = "select count(*) from User u where u.panNumber = :panNumber")
    Long findByPanNumberCount(@Param("panNumber") String panNumber);

    long countByIsActive(Boolean isActive);
//    long count();

}