package com.octal.actorPay.service;

import com.octal.actorPay.dto.GlobalSettingsDTO;
import org.springframework.stereotype.Component;

@Component
public interface GlobalSettingsService {

    GlobalSettingsDTO getGlobalSettings();

    void createOrUpdateGlobalSettings(GlobalSettingsDTO gsDTO);
}