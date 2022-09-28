package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SettingDTO;
import com.octal.actorPay.dto.ShippingAddressDTO;
import com.octal.actorPay.entities.ShippingAddress;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserSetting;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.SettingRepository;
import com.octal.actorPay.repositories.ShippingAddressRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.CommonService;
import com.octal.actorPay.service.SettingService;
import com.octal.actorPay.service.ShippingAddressService;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.ShipmentAddressTransformer;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class SettingServiceImpl implements SettingService {


    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonService commonService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean saveUserSetting(String currentUserEmail, SettingDTO settingDTO) {
        UserSetting record;
        User user = ruleValidator.checkPresence(userRepository.findByEmail(currentUserEmail).get(), "User not found : " + currentUserEmail);
        Optional<UserSetting> setting = settingRepository
                .findByUser(user);
        if(setting.isPresent()){
            record = setting.get();
            record.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            record.setNotification(settingDTO.getNotification());
        }else{
            record = new UserSetting();
            record.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            record.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            record.setActive(true);
            record.setUser(user);
            record.setNotification(settingDTO.getNotification());
        }
        settingRepository.save(record);
        return record.getNotification();
    }

}
