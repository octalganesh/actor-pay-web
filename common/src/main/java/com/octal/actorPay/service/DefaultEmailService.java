package com.octal.actorPay.service;

import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface DefaultEmailService<T> {

    void sendEmailOnNewUserRegistrationByAdmin(T user, String activationToken);

    void sendEmailOnSignUp(T user, String activationToken);


}