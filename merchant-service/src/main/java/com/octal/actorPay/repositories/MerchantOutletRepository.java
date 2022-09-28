package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.MerchantOutlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MerchantOutletRepository extends JpaRepository<MerchantOutlet,String> {


    long countByMerchantIdAndOutletId(String merchantId,String outletId);

    MerchantOutlet findByMerchantIdAndOutletId(String merchantId,String outletId);

    long countByOutletId(String outletId);

    @Query(value = "select count(*) from merchant_outlet m where m.merchant_id = :merchantId and m.outlet_id != :outletId" , nativeQuery = true)
    long countByMerchantIdAndNotOutletId(@Param("merchantId") String merchantId, @Param("outletId") String outletId);

    @Query(value = "select count(*) from merchant_outlet m where m.outlet_id = :outletId and m.merchant_id != :merchantId" , nativeQuery = true)
    long countByOutletIdAndNotMerchantId(@Param("merchantId") String merchantId, @Param("outletId") String outletId);

}
