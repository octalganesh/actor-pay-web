package com.octal.actorPay.service;

import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface EmailService extends DefaultEmailService<User> {

    void sendEmailForForgetPasswordLink(User user, String token);

    void sendEmailOnNewUserRegistrationByAdmin(User user, String activationToken, String Password);


}