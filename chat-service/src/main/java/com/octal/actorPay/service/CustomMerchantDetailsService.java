package com.octal.actorPay.service;

import com.octal.actorPay.config.MerchantService;
import com.octal.actorPay.dto.AuthUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomMerchantDetailsService {

    @Autowired
    private MerchantService merchantService;

    public AuthUserDTO loadUserByUsername(String email) throws UsernameNotFoundException {

        // call user-service api and get user details
        return merchantService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email id:" + email + " not found"));

    }
}