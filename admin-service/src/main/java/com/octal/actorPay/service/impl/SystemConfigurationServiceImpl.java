package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.MerchantType;
import com.octal.actorPay.constants.SettingsType;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.request.SystemConfigRequest;
import com.octal.actorPay.entities.SystemConfiguration;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.AccessDeniedException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.SystemConfigurationRepositories;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.SystemConfigurationService;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemConfigurationRepositories systemConfigurationRepositories;

    @Autowired
    private MerchantClient merchantClient;
    private Function<SystemConfiguration, SystemConfigurationDTO> SYSTEM_CONFIG_TO_DTO = (config) -> {
        SystemConfigurationDTO systemConfigurationDTO = new SystemConfigurationDTO();
        systemConfigurationDTO.setId(config.getId());
        systemConfigurationDTO.setCreatedAt(config.getCreatedAt());
        systemConfigurationDTO.setActive(config.getActive());
        systemConfigurationDTO.setParamValue(config.getParamValue());
        systemConfigurationDTO.setParamDescription(config.getParamDescription());
        systemConfigurationDTO.setParamName(config.getParamName());
        systemConfigurationDTO.setSettingsType(config.getSettingsType());
        if (config.getModule() != null) {
            systemConfigurationDTO.setModule(config.getModule().name());
        }
        return systemConfigurationDTO;
    };

    @Override
    public SystemConfigurationDTO getSystemConfigurationDetails(String configId, String actor) {
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(actor), "User not found for provided email id :" + actor);
        beforeAccessSystemConfiguration(currentUser);

        Optional<SystemConfiguration> existingConfig = ruleValidator.checkPresence(systemConfigurationRepositories.findById(configId), "System configuration details not found given id");
        return existingConfig.map(systemConfiguration -> SYSTEM_CONFIG_TO_DTO.apply(systemConfiguration)).orElse(null);
    }

    @Override
    public SystemConfigurationDTO getSystemConfigurationDetailsByKey(String key) {
//        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(actor), "User not found for provided email id :" + actor);
//        beforeAccessSystemConfiguration(currentUser);
        Optional<SystemConfiguration> existingConfig = systemConfigurationRepositories
                .findSystemConfigurationByParamName(key);
        if(existingConfig == null) {
            throw new ActorPayException(String.format("System configuration details not found given key %s",key));
        }
        return existingConfig.map(systemConfiguration -> SYSTEM_CONFIG_TO_DTO.apply(systemConfiguration)).orElse(null);

    }

    @Override
    public List<SystemConfigurationDTO> getSystemConfigurationDetailsByKeys(List<String> keys) {
        List<SystemConfiguration> existingConfigs = systemConfigurationRepositories.findSystemConfigurationByParamNameIn(keys);
        List<SystemConfigurationDTO> systemConfigurationDTOS = new ArrayList<>();
        for (SystemConfiguration systemConfiguration : existingConfigs) {
            SystemConfigurationDTO systemConfigurationDTO = SYSTEM_CONFIG_TO_DTO.apply(systemConfiguration);
            systemConfigurationDTOS.add(systemConfigurationDTO);
        }
        return systemConfigurationDTOS;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SystemConfiguration createSystemConfiguration(SystemConfigurationDTO systemConfigurationDTO, String actor) {
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(actor), "User not found for provided email id :" + actor);
        beforeAccessSystemConfiguration(currentUser);
        Optional<SystemConfiguration> systemConfig = systemConfigurationRepositories.findSystemConfigurationByParamName(systemConfigurationDTO.getParamName());
        if (systemConfig.isPresent()) {
            throw new ActorPayException("Global Setting Key already exist " + systemConfigurationDTO.getParamName());
        }

        SystemConfiguration systemConfiguration = new SystemConfiguration();
        systemConfiguration.setCreator(currentUser.get().getId());
        systemConfiguration.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        mapSystemConfigurationValues(systemConfigurationDTO, systemConfiguration);
        systemConfiguration.setActive(Boolean.TRUE);
        systemConfiguration.setDeleted(Boolean.FALSE);
        systemConfigurationRepositories.save(systemConfiguration);
        return systemConfiguration;
    }

    private void mapSystemConfigurationValues(SystemConfigurationDTO systemConfigurationDTO, SystemConfiguration systemConfiguration) {
        systemConfiguration.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        systemConfiguration.setParamDescription(systemConfigurationDTO.getParamDescription());
        systemConfiguration.setParamName(systemConfigurationDTO.getParamName());
        systemConfiguration.setParamValue(systemConfigurationDTO.getParamValue());
        if (systemConfigurationDTO.getSettingsType() == null) {
            systemConfiguration.setSettingsType(SettingsType.ADMIN_SETTING);
        } else {
            systemConfiguration.setSettingsType(systemConfigurationDTO.getSettingsType());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSystemConfiguration(SystemConfigurationDTO systemConfigurationDTO, String actor) {
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(actor), "User not found for provided email id :" + actor);
        beforeAccessSystemConfiguration(currentUser);

        Optional<SystemConfiguration> existingConfig = systemConfigurationRepositories.findById(systemConfigurationDTO.getId());
        if (!existingConfig.isPresent()) {
            throw new RuntimeException(String.format("Invalid Global Setting id %d ", systemConfigurationDTO.getId()));
        }
        String existingKey = existingConfig.get().getParamName();
        String newKey = systemConfigurationDTO.getParamName();
        if (!existingKey.equals(newKey)) {
            throw new RuntimeException(String.format("Key can't be modified %s. Delete and Recreate new Key", existingKey));
        }
        Optional<SystemConfiguration> systemConfig = systemConfigurationRepositories.findSystemConfigurationByParamName(systemConfigurationDTO.getParamName());

        if (systemConfig.isPresent() && !existingConfig.get().getId().equals(systemConfigurationDTO.getId())) {
            throw new ActorPayException("Param name already exist for given module " + systemConfigurationDTO.getParamName());
        }
        mapSystemConfigurationValues(systemConfigurationDTO, existingConfig.get());
        systemConfigurationRepositories.save(existingConfig.get());
        SystemConfiguration systemConfigEntity = existingConfig.get();
        if (systemConfigEntity.getSettingsType() == SettingsType.MERCHANT_SETTING) {
            if (CommonConstant.merchantSettings.contains(systemConfigurationDTO.getParamName())) {
                SystemConfigRequest systemConfigRequest = new SystemConfigRequest();
                systemConfigRequest.setMerchantType(MerchantType.BRONZE);
                systemConfigRequest.setParamName(systemConfigurationDTO.getParamName());
                systemConfigRequest.setParamValue(systemConfigurationDTO.getParamValue());
                merchantClient.updateMerchantSettingsOnGlobalSettingsUpdate(systemConfigRequest);
            }
        }
    }

    @Override
    public PageItem<SystemConfigurationDTO> getAllSystemConfigurationPaged(PagedItemInfo pagedInfo, String actor) {
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(actor), "User not found for provided email id :" + actor);
        beforeAccessSystemConfiguration(currentUser);
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(SystemConfiguration.class, pagedInfo);

        Page<SystemConfiguration> pagedResult = systemConfigurationRepositories.findAll(pageRequest);
        List<SystemConfigurationDTO> content = pagedResult.getContent().stream().map(SYSTEM_CONFIG_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);

    }

    @Override
    public void deleteSystemConfiguration(List<String> ids, String currentUse) throws InterruptedException {
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(currentUse), "User not found for provided email id :" + currentUse);
        beforeAccessSystemConfiguration(currentUser);
        List<SystemConfiguration> systemConfigurations = systemConfigurationRepositories.findAllById(ids);
        systemConfigurationRepositories.deleteAll(systemConfigurations);
//        List<SystemConfiguration> systemConfigurations = systemConfigurationRepositories.findAllById(ids);
//        systemConfigurations.forEach(systemConfiguration -> {
//            systemConfiguration.setDeleted(Boolean.TRUE);
//            systemConfiguration.setActive(Boolean.FALSE);
//            systemConfiguration.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
//            systemConfigurationRepositories.save(systemConfiguration);
//        });
    }

    private void beforeAccessSystemConfiguration(Optional<User> currentUser) {
        if (currentUser.isPresent() && !currentUser.get().getAdmin()) {
            throw new AccessDeniedException("Access Denied, Only admin can access system configuration resource");
        }
    }

}