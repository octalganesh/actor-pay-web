package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.PromoCodesHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromoCodesHistoryRepository extends JpaRepository<PromoCodesHistory, String> {

    List<PromoCodesHistory> findByPromoCode(String promoCode);
    List<PromoCodesHistory> findByOrderId(String orderId);

}
