package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.MerchantType;
import com.octal.actorPay.entities.AbstractPersistable;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.MerchantSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MerchantSettingsRepository extends JpaRepository<MerchantSettings,String> {

        MerchantSettings findByParamNameAndMerchantDetailsAndDeletedFalse(String key, MerchantDetails merchantDetails);

        List<MerchantSettings> findByDeletedFalseAndMerchantDetailsAndParamNameIn(MerchantDetails merchantDetails,List<String> keys);

        @Modifying
        @Query("update MerchantSettings ms set ms.paramValue = :paramValue where ms.paramName = :paramName and " +
                "ms.merchantDetails.id in (select m.id from MerchantDetails m where m.merchantType = :merchantType)")
        void updateMerchantSettingByMerchantType(
                @Param("paramName") String paramName,
                @Param("paramValue") String paramValue,
                @Param("merchantType") MerchantType merchantType);
}
