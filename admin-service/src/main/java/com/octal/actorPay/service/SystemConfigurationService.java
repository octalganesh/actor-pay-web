package com.octal.actorPay.service;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.entities.SystemConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SystemConfigurationService {

    SystemConfigurationDTO getSystemConfigurationDetails(String configuratinId, String actor);

    SystemConfigurationDTO getSystemConfigurationDetailsByKey(String key);

    SystemConfiguration createSystemConfiguration(SystemConfigurationDTO systemConfigurationDTO, String actor);

    void updateSystemConfiguration(SystemConfigurationDTO systemConfigurationDTO, String actor);

    PageItem<SystemConfigurationDTO> getAllSystemConfigurationPaged(PagedItemInfo pagedInfo, String actor);

    void deleteSystemConfiguration(List<String> ids, String currentUse ) throws InterruptedException;

    List<SystemConfigurationDTO> getSystemConfigurationDetailsByKeys(List<String> keys);


}