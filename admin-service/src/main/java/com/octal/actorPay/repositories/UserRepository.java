package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.User;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {

    Optional<User> findByEmailAndIsActiveTrueAndDeletedFalse(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    Optional<User> findUserByEmailOrContactNumber(String email, String contactNumber);

    //Page<User> findAllByIsAdminIsFalse(Specification<User> specification, Pageable pageable);

    Page<User> findAll(Specification<User> specification, Pageable page);

    List<User> findAll(Specification<User> specification);

    LocalDateTime findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


    Optional<User> findUserByContactNumber(String contactNumber);

    @Query(value = "select * from users u where contact_number = :userIdentity  or  email = :userIdentity or " +
            "upi_qr_code = :userIdentity or id = :userIdentity", nativeQuery = true)
    User getUserDetailsByEmilOrContactNO(@Param("userIdentity") String userIdentity);
}