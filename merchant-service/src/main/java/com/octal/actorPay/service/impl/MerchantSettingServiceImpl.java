package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.MerchantSettingsDTO;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.MerchantSettings;
import com.octal.actorPay.repositories.MerchantDetailsRepository;
import com.octal.actorPay.repositories.MerchantSettingsRepository;
import com.octal.actorPay.service.MerchantSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class MerchantSettingServiceImpl implements MerchantSettingService {

    private MerchantSettingsRepository merchantSettingsRepository;

    private MerchantDetailsRepository merchantDetailsRepository;

    public MerchantSettingServiceImpl(MerchantSettingsRepository merchantSettingsRepository,
                                      MerchantDetailsRepository merchantDetailsRepository) {
        this.merchantSettingsRepository = merchantSettingsRepository;
        this.merchantDetailsRepository = merchantDetailsRepository;
    }

    @Override
    public MerchantSettingsDTO findMerchantSettingsByKey(String key, String merchantId) {

        MerchantDetails merchantDetails = merchantDetailsRepository.findByIdAndDeletedFalse(merchantId);
        MerchantSettings merchantSettings = merchantSettingsRepository
                .findByParamNameAndMerchantDetailsAndDeletedFalse(key, merchantDetails);
        MerchantSettingsDTO merchantSettingsDTO = mapToDTO(merchantSettings);
        return merchantSettingsDTO;
    }

    @Override
    public List<MerchantSettingsDTO> getMerchantSettings(List<String> keys, String merchantId) {
        MerchantDetails merchantDetails = merchantDetailsRepository.findByIdAndDeletedFalse(merchantId);
        List<MerchantSettingsDTO> merchantSettingsDTOS = new ArrayList<>();
        List<MerchantSettings> merchantSettingsList = merchantSettingsRepository
                .findByDeletedFalseAndMerchantDetailsAndParamNameIn(merchantDetails, keys);
        for (MerchantSettings merchantSettings : merchantSettingsList) {
            MerchantSettingsDTO merchantSettingsDTO = mapToDTO(merchantSettings);
            merchantSettingsDTOS.add(merchantSettingsDTO);
        }
        return merchantSettingsDTOS;
    }

    @Transactional
    @Override
    public List<MerchantSettingsDTO> addMerchantSettings(List<SystemConfigurationDTO> systemConfigurationDTOS, String merchantId) {
        List<MerchantSettings> merchantSettingsList = new ArrayList<>();
        List<MerchantSettingsDTO> merchantSettingsDTOList = new ArrayList<>();
        try {
            MerchantDetails merchantDetails = merchantDetailsRepository.findByIdAndDeletedFalse(merchantId);
            for (SystemConfigurationDTO systemConfigurationDTO : systemConfigurationDTOS) {
                MerchantSettings merchantSettings = mapToEntity(systemConfigurationDTO);
                merchantSettings.setMerchantDetails(merchantDetails);
                merchantSettingsList.add(merchantSettings);
            }
            List<MerchantSettings> merchantSettings = merchantSettingsRepository.saveAll(merchantSettingsList);
            for (MerchantSettings settings : merchantSettings) {
                MerchantSettingsDTO settingsDTO = mapToDTO(settings);
                merchantSettingsDTOList.add(settingsDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return merchantSettingsDTOList;
    }

    private MerchantSettingsDTO mapToDTO(MerchantSettings merchantSettings) {

        MerchantSettingsDTO merchantSettingsDTO = new MerchantSettingsDTO();
        merchantSettingsDTO.setActive(merchantSettings.isActive());
        merchantSettingsDTO.setId(merchantSettings.getId());
        merchantSettingsDTO.setParamValue(merchantSettings.getParamValue());
        merchantSettingsDTO.setParamName(merchantSettings.getParamName());
        merchantSettingsDTO.setParamDescription(merchantSettings.getParamDescription());
        merchantSettingsDTO.setCreatedAt(merchantSettings.getCreatedAt());
        merchantSettingsDTO.setUpdatedAt(merchantSettings.getUpdatedAt());
        return merchantSettingsDTO;
    }

    private MerchantSettings mapToEntity(SystemConfigurationDTO systemConfigurationDTO) {

        MerchantSettings merchantSettings = new MerchantSettings();
        merchantSettings.setActive(Boolean.TRUE);
        merchantSettings.setParamValue(systemConfigurationDTO.getParamValue());
        merchantSettings.setParamName(systemConfigurationDTO.getParamName());
        merchantSettings.setParamDescription(systemConfigurationDTO.getParamDescription());
        merchantSettings.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return merchantSettings;
    }
}
