package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.EmailDTO;
import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.service.UserEmailService;
import com.octal.actorPay.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserEmailServiceImpl implements UserEmailService {

    @Autowired
    private NotificationClient notificationClient;

    @Value("${user.activation.link.url}")
    private String activationLink;

    @Value("${user.forget.password.link}")
    private String userResetPasswordUrl;

    @Value("${admin.dispute.email}")
    private String adminEmail;

    @Override
    public void sendEmailOnNewUserRegistrationByAdmin(User user, String activationToken) throws Exception {
        EmailDTO mail = new EmailDTO();
        mail.setMailTo(user.getEmail());
        mail.setSubject("[ActorPay] Welcome to ActorPay");
        mail.setTemplateName("ADD_NEW_USER_BY_ADMIN");
        Map<String, Object> model = new HashMap<String, Object>();
        // TODO added below properties for testing purpose only, will replace once we got proper template
        model.put("name", user.getFirstName() + " " + user.getLastName());
        model.put("password", CommonUtils.getRandomNumber(1000000, 7));
        model.put("activationLink", activationLink.replace("<token>", activationToken));
        model.put("location", "India");
        model.put("sign", "ActorPay Team");
        mail.setProps(model);
        notificationClient.sendEmailNotification(mail);
    }


    @Override
    @Async
    public void sendEmailOnSignUp(User user, String activationToken) {
        EmailDTO mail = new EmailDTO();
        // TODO we have added hardcode for testing purpose only
        mail.setMailTo(user.getEmail());
        // Read from config server
        mail.setTemplateName("CUSTOMER_SIGNUP");
        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getFirstName() + " " + user.getLastName());
        model.put("activationLink", activationLink.replace("<token>", activationToken));
        model.put("location", "India");
        model.put("sign", "ActorPay Team");
        mail.setProps(model);
        notificationClient.sendEmailNotification(mail);
    }

    @Override
    @Async
    public void sendEmailForForgetPasswordLink(User user, String token) {
        EmailDTO mail = new EmailDTO();
        mail.setMailTo(user.getEmail());
        // TODO added for testing purpose, will replace with email service
        mail.setSubject("[ActorPay] Forget password");
        mail.setTemplateName("RESET_PASSWORD");
        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getFirstName() + " " + user.getLastName());
        model.put("location", "India");
        model.put("sign", "ActorPay Team");
        model.put("link", userResetPasswordUrl.replace("<token>", token));
        mail.setProps(model);

        notificationClient.sendEmailNotification(mail);
    }

    @Override
    public void sendEmailOnDisputeAlarm(DisputeItem disputeItem) {
        EmailDTO mail = new EmailDTO();
        mail.setMailTo(adminEmail);
        mail.setSubject("DISPUTE-NOTIFICATION");
        mail.setTemplateName("DISPUTE-NOTIFICATION");
        Map<String, Object> model = new HashMap<>();
        model.put("name", "user.getMerchantDetails().getBusinessName()");
//        model.put("password", CommonUtils.getRandomNumber(1000000));
        model.put("location", "India");
        model.put("sign", "ActorPay Team");
        mail.setProps(model);
        notificationClient.sendEmailNotification(mail);
    }
}