package com.octal.actorPay.service.impl;

import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.CommonService;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommonServiceImpl implements CommonService {


    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> findUserByEmail(String email) {
       return ruleValidator.checkPresence(userRepository.findByEmail(email),
                "User not found for the given email id: " + email);
    }
}