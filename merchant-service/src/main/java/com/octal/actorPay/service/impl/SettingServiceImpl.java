package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.SettingDTO;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.SettingService;
import com.octal.actorPay.utils.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class SettingServiceImpl implements SettingService {

//    @Autowired
//    private SettingRepository settingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonService commonService;

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean saveUserSetting(String currentUserEmail, SettingDTO settingDTO) {
        Optional<User> user = userRepository.findByEmail(currentUserEmail);
 //       if(user.isPresent()){
            user.get().setNotificationActive(settingDTO.getNotification());
//        user.get().setNotificationActive(true);
//        }else{
//            user.get().setActive(true);
//            user.get().setNotificationActive(settingDTO.getNotification());
//        }
        userRepository.save(user.get());
        return user.get().isNotificationActive();
    }

}
