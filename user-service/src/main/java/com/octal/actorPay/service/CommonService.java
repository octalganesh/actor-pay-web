package com.octal.actorPay.service;

import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CommonService {
    Optional<User> findUserByEmail(String email);


}