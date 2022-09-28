package com.octal.actorPay.service;

import com.octal.actorPay.dto.SettingDTO;
import org.springframework.stereotype.Component;

@Component
public interface SettingService {

    Boolean saveUserSetting(String currentMerchantEmail, SettingDTO settingDTO);
}
