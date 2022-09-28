package com.octal.actorPay.service;


import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SettingDTO;
import com.octal.actorPay.dto.ShippingAddressDTO;
import org.springframework.stereotype.Component;

@Component
public interface SettingService {

    Boolean saveUserSetting(String currentUserEmail, SettingDTO settingDTO);

}
