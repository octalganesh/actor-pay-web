package com.octal.actorPay.listners;

import com.octal.actorPay.client.NotificationClient;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.listners.events.RegistrationCompleteEvent;
import com.octal.actorPay.service.DefaultEmailService;
import com.octal.actorPay.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * RegistrationListener - whenever a new user is created, it will receive a event,
 * once the event occurred, it will execute onApplicationEvent method and then it will send email to newly registered user
 */
@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private DefaultEmailService<User> emailService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        final User user = event.getUser();
        UserOtpVerification userVerificationToken = userVerificationService.generateVerificationLinkOnUserSignup(user);
        emailService.sendEmailOnSignUp(user, userVerificationToken.getToken());

    }
}