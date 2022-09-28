package com.octal.actorPay.repositories;


import com.octal.actorPay.entities.Offer;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer,String>, JpaSpecificationExecutor<Offer> {

    Optional<Offer> findByIdAndUserAndDeletedFalse(String offerId,User user);
    Optional<Offer> findByIdAndDeletedFalse(String offerId);
    Optional<Offer> findByOfferTitleAndUserAndDeletedFalse(String titleName,User user);
    Page<Offer> findByUserAndDeletedFalse(Pageable pageable,User user);
//    Page<Offer> findByIsPublishedAnd
    @Modifying
    @Query("update Offer off set off.deleted=true,off.isActive=false where off.isActive=true and off.user=:user and off.id = :offerId")
    void deleteOffer(@Param("offerId") String offerId,@Param("user") User user);
    Page<Offer> findOfferByIsActiveAndVisibilityLevelAndDeletedFalse(Pageable pageable,boolean isActive,String visibilityLevel);

    @Modifying
    @Query("update Offer offer set offer.isActive  = :isActivate where offer.id in (:offerIds) and offer.user = :user")
    void activateOrDeActivate(@Param("offerIds") List<String> offerIds, @Param("isActivate") boolean isPublished,@Param("user") User user);

    //Optional<Offer> findByPromoCodeAndDeletedFalse(@Param("promoCode") String promoCode);

    Optional<Offer> findByOfferCodeAndDeletedFalse(@Param("offerCode") String offerCode);
    Optional<Offer> findByOfferCodeAndDeleted(String offerCode,Boolean deleted);
}
