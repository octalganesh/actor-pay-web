package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDocumentRepository extends JpaRepository<UserDocument, String> , JpaSpecificationExecutor<UserDocument> {

//    Optional<UserDocument> findByUserAndDocType(User user, String docType);
    @Query(value = "select userDoc from UserDocument userDoc where userDoc.user.id= :userId")
    Optional<UserDocument> findByUserId(@Param("userId") String userId);

    List<UserDocument> findByUserAndDocTypeIn(User user, List<String> docTypes);

    @Query("select userDoc from UserDocument userDoc where userDoc.user.id=:userId")
    List<UserDocument> findByUser(@Param("userId") String userId);

    @Query("select userDoc from UserDocument userDoc where userDoc.user.id=:userId and userDoc.docType=:docType")
    Optional<UserDocument> findByUserAndDocType(@Param("userId") String userId,@Param("docType") String docType);

    @Query(value = "select count(*) from user_document where doc_type in ('AADHAAR','PAN') and " +
            "verified_status = 'COMPLETED' and user_id = ?1", nativeQuery = true)
    Long findIsVerified(String userId);
}
