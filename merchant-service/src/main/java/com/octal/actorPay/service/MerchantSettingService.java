package com.octal.actorPay.service;

import com.octal.actorPay.dto.MerchantSettingsDTO;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.MerchantSettings;

import java.util.List;

public interface MerchantSettingService {


    MerchantSettingsDTO findMerchantSettingsByKey(String key, String merchantId);
    List<MerchantSettingsDTO> getMerchantSettings(List<String> keys,String merchantId);
    List<MerchantSettingsDTO> addMerchantSettings(List<SystemConfigurationDTO> systemConfigurationDTOS,String merchantId);
}
