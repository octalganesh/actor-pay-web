package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.MerchantSubMerchantAssoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantSubMerchantAssocRepository extends JpaRepository<MerchantSubMerchantAssoc,String> {

    MerchantSubMerchantAssoc findBySubmerchantId(String submerchantId);

    List<MerchantSubMerchantAssoc> findByMerchantId(String merchantId);

}
